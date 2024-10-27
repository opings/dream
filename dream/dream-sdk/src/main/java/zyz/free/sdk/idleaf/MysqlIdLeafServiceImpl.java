/**
 * @author zyz
 * @since 2020-03-18 09:29
 */
package zyz.free.sdk.idleaf;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import zyz.free.util.AssertUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class MysqlIdLeafServiceImpl implements IdLeafService {


    private String bizTag;
    private JdbcTemplate jdbcTemplate;
    private boolean asyncLoadingSegment;

    private ExecutorService taskExecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());


    /**
     * 这两段用来存储每次拉升之后的最大值
     */
    private volatile IdSegment[] segment = new IdSegment[2];
    @Setter
    private volatile boolean sw;
    private AtomicLong currentId;
    private ReentrantLock lock = new ReentrantLock();
    private volatile FutureTask<Boolean> asyncLoadSegmentTask = null;


    public MysqlIdLeafServiceImpl(String bizTag, JdbcTemplate jdbcTemplate, boolean asyncLoadingSegment) {

        AssertUtil.notBlank(bizTag, () -> "bizTag must be not null");
        AssertUtil.notNull(jdbcTemplate, () -> "jdbcTemplate null");

        this.bizTag = bizTag;
        this.jdbcTemplate = jdbcTemplate;
        this.asyncLoadingSegment = asyncLoadingSegment;
        segment[0] = doUpdateNextSegment(bizTag);
        setSw(false);
        currentId = new AtomicLong(segment[index()].getMinId());
    }

    @Override
    public Long genId() {
        if (asyncLoadingSegment) {
            return asyncGetId();
        } else {
            return synGetId();
        }
    }

    private Long asyncGetId() {

        if (segment[index()].getMiddleId() <= currentId.longValue() && isNotLoadOfNextSegment()
                && asyncLoadSegmentTask == null) {
            try {
                lock.lock();
                if (segment[index()].getMiddleId() <= currentId.longValue()) {
                    // 前一段使用了50%
                    asyncLoadSegmentTask = new FutureTask<>(() -> {
                        final int currentIndex = reIndex();
                        segment[currentIndex] = doUpdateNextSegment(bizTag);
                        return true;
                    });
                    taskExecutor.submit(asyncLoadSegmentTask);
                }

            } finally {
                lock.unlock();
            }
        }

        if (segment[index()].getMaxId() <= currentId.longValue()) {
            try {
                lock.lock();
                if (segment[index()].getMaxId() <= currentId.longValue()) {

                    boolean loadingResult;
                    try {
                        loadingResult = asyncLoadSegmentTask.get(500, TimeUnit.MILLISECONDS);
                        if (loadingResult) {
                            // 切换
                            setSw(!isSw());
                            // 进行切换
                            currentId = new AtomicLong(segment[index()].getMinId());
                            asyncLoadSegmentTask = null;
                        }
                    } catch (Exception e) {
                        loadingResult = false;
                        asyncLoadSegmentTask = null;
                    }
                    if (!loadingResult) {
                        while (isNotLoadOfNextSegment()) {
                            // 强制同步切换
                            final int currentIndex = reIndex();
                            segment[currentIndex] = doUpdateNextSegment(bizTag);
                        }
                        //切换
                        setSw(!isSw());
                        //进行切换
                        currentId = new AtomicLong(segment[index()].getMinId());

                    }
                }
            } finally {
                lock.unlock();
            }
        }

        return currentId.incrementAndGet();

    }

    private long synGetId() {
        if (segment[index()].getMiddleId() <= currentId.longValue() && isNotLoadOfNextSegment()) {
            try {
                lock.lock();
                if (segment[index()].getMiddleId() <= currentId.longValue() && isNotLoadOfNextSegment()) {
                    // 使用50%以上，并且没有加载成功过，就进行加载
                    final int currentIndex = reIndex();
                    segment[currentIndex] = doUpdateNextSegment(bizTag);
                }
            } finally {
                lock.unlock();
            }
        }

        // 需要进行切换了
        if (segment[index()].getMaxId() <= currentId.longValue()) {
            try {
                lock.lock();
                if (segment[index()].getMaxId() <= currentId.longValue()) {
                    while (isNotLoadOfNextSegment()) {
                        // 使用50%以上，并且没有加载成功过，就进行加载,直到成功
                        final int currentIndex = reIndex();
                        segment[currentIndex] = doUpdateNextSegment(bizTag);
                    }
                    // 切换
                    setSw(!isSw());
                    //进行切换
                    currentId = new AtomicLong(segment[index()].getMinId());

                }

            } finally {
                lock.unlock();
            }
        }
        return currentId.incrementAndGet();

    }

    private boolean isNotLoadOfNextSegment() {
        if (segment[reIndex()] == null) {
            return true;
        }
        if (segment[reIndex()].getMinId() < segment[index()].getMinId()) {
            return true;
        }
        return false;
    }

    private boolean isSw() {
        return sw;
    }

    private int index() {
        if (isSw()) {
            return 1;
        } else {
            return 0;
        }
    }

    private int reIndex() {
        if (isSw()) {
            return 0;
        } else {
            return 1;
        }
    }

    private IdSegment doUpdateNextSegment(String bizTag) {
        return updateId(bizTag);
    }

    private IdSegment updateId(String bizTag) {

        String querySql = "select p_step ,max_id ,last_update_time,current_update_time from id_segment where biz_tag=?";
        String updateSql = "update id_segment set max_id=?,last_update_time=?,current_update_time=now() where biz_tag=? and max_id=?";


        final IdSegment currentSegment = new IdSegment();
        this.jdbcTemplate.query(querySql, new String[]{bizTag}, new RowCallbackHandler() {

            @Override
            public void processRow(ResultSet rs) throws SQLException {

                Long step = null;
                Long currentMaxId = null;
                step = rs.getLong("p_step");
                currentMaxId = rs.getLong("max_id");

                Date lastUpdateTime = new Date();
                if (rs.getTimestamp("last_update_time") != null) {
                    lastUpdateTime = (Date) rs.getTimestamp("last_update_time");
                }

                Date currentUpdateTime = new Date();
                if (rs.getTimestamp("current_update_time") != null) {
                    currentUpdateTime = (Date) rs.getTimestamp("current_update_time");
                }

                currentSegment.setStep(step);
                currentSegment.setMaxId(currentMaxId);
                currentSegment.setLastUpdateTime(lastUpdateTime);
                currentSegment.setCurrentUpdateTime(currentUpdateTime);

            }
        });
        Long newMaxId = currentSegment.getMaxId() + currentSegment.getStep();
        int row = this.jdbcTemplate.update(updateSql,
                new Object[]{newMaxId, currentSegment.getCurrentUpdateTime(), bizTag, currentSegment.getMaxId()});
        if (row == 1) {
            IdSegment newSegment = new IdSegment();
            newSegment.setStep(currentSegment.getStep());
            newSegment.setMaxId(newMaxId);
            log.info("获取分布式id号段:bizTag={},maxId={}", bizTag, newMaxId);
            return newSegment;
        } else {
            return updateId(bizTag);
        }

    }

}
