package zyz.free.service.genid;

import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import zyz.free.data.entity.IdSegmentEntity;
import zyz.free.data.mapper.IdSegmentEntityMapper;
import zyz.free.util.AssertUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;


@Log4j2
public class MysqlGenIdLeafServiceImpl {

    private String bizTag;
    // 这两段用来存储每次拉升之后的最大值
    private volatile IdSegment[] segment = new IdSegment[2];
    // 标记使用的segment的下标，false:segment[0]  true:segment[1]
    private volatile SegmentIndexEnum segmentIndexEnum;
    // 当前id
    private volatile AtomicLong currentId;
    private final ReentrantLock lock = new ReentrantLock();
    private final IdSegmentEntityMapper idSegmentEntityMapper;

    private boolean asyncLoadingSegment;
    private ExecutorService taskExecutor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    private volatile FutureTask<Boolean> asyncLoadSegmentTask = null;

    /**
     * @param bizTag                业务tag
     * @param idSegmentEntityMapper 也可使用{@link org.springframework.jdbc.core.JdbcTemplate}代替
     */
    public MysqlGenIdLeafServiceImpl(String bizTag,
                                     IdSegmentEntityMapper idSegmentEntityMapper,
                                     boolean asyncLoadingSegment) {

        AssertUtil.notBlank(bizTag, () -> "bizTag must be not null");
        AssertUtil.notNull(idSegmentEntityMapper, () -> "idSegmentEntityMapper null");

        this.bizTag = bizTag;
        this.idSegmentEntityMapper = idSegmentEntityMapper;
        segment[0] = doUpdateNextSegment(bizTag);
        segmentIndexEnum = SegmentIndexEnum.first;
        currentId = new AtomicLong(segment[currentIndex().getIndex()].getMinId());
        this.asyncLoadingSegment = asyncLoadingSegment;
    }

    public Long genId() {
        if (asyncLoadingSegment) {
            return asyncGetId();
        } else {
            return syncGetId();
        }
    }


    public long asyncGetId() {
        // 生成下一个Segment. 使用50%以上，并且没有加载成功过，就进行加载
        if (segment[currentIndex().getIndex()].getMiddleId() <= currentId.longValue()
                && isNotLoadOfNextSegment()
                && asyncLoadSegmentTask != null) {
            try {
                lock.lock();
                if (segment[currentIndex().getIndex()].getMiddleId() <= currentId.longValue()
                        && isNotLoadOfNextSegment()
                        && asyncLoadSegmentTask != null) {
                    // 异步加载
                    asyncLoadSegmentTask = new FutureTask<>(() -> {
                        final int currentIndex = swappedIndex().getIndex();
                        segment[currentIndex] = doUpdateNextSegment(bizTag);
                        return true;
                    });
                    taskExecutor.submit(asyncLoadSegmentTask);
                }
            } finally {
                lock.unlock();
            }
        }

        // 需要进行Segment切换了
        if (segment[currentIndex().getIndex()].getMaxId() <= currentId.longValue()) {
            try {
                lock.lock();
                if (segment[currentIndex().getIndex()].getMaxId() <= currentId.longValue()) {
                    boolean loadingResult;
                    try {
                        loadingResult = asyncLoadSegmentTask.get(500, TimeUnit.MILLISECONDS);
                        if (loadingResult) {
                            // 切换
                            swapSegment();
                            //进行切换
                            currentId = new AtomicLong(segment[currentIndex().getIndex()].getMinId());
                            asyncLoadSegmentTask = null;
                        }
                    } catch (Exception e) {
                        loadingResult = false;
                        asyncLoadSegmentTask = null;
                    }

                    if (!loadingResult) {
                        while (isNotLoadOfNextSegment()) {
                            // 当前segment已经用完，并且没有加载成功过，就进行加载,直到成功
                            final int currentIndex = swappedIndex().getIndex();
                            segment[currentIndex] = doUpdateNextSegment(bizTag);
                        }
                        // 切换
                        swapSegment();
                        //进行切换
                        currentId = new AtomicLong(segment[currentIndex().getIndex()].getMinId());
                    }
                }
            } finally {
                lock.unlock();
            }
        }

        // 获取gen ID
        return currentId.incrementAndGet();
    }


    public long syncGetId() {
        // 生成下一个IdSegment
        if (segment[currentIndex().getIndex()].getMiddleId() <= currentId.longValue() && isNotLoadOfNextSegment()) {
            try {
                lock.lock();
                if (segment[currentIndex().getIndex()].getMiddleId() <= currentId.longValue() && isNotLoadOfNextSegment()) {
                    // 使用50%以上，并且没有加载成功过，就进行加载
                    final int currentIndex = swappedIndex().getIndex();
                    segment[currentIndex] = doUpdateNextSegment(bizTag);
                }
            } finally {
                lock.unlock();
            }
        }

        // 需要进行IdSegment切换了
        if (segment[currentIndex().getIndex()].getMaxId() <= currentId.longValue()) {
            try {
                lock.lock();
                if (segment[currentIndex().getIndex()].getMaxId() <= currentId.longValue()) {
                    while (isNotLoadOfNextSegment()) {
                        // 使用50%以上，并且没有加载成功过，就进行加载,直到成功
                        final int currentIndex = swappedIndex().getIndex();
                        segment[currentIndex] = doUpdateNextSegment(bizTag);
                    }
                    // 切换
                    swapSegment();
                    //进行切换
                    currentId = new AtomicLong(segment[currentIndex().getIndex()].getMinId());
                }

            } finally {
                lock.unlock();
            }
        }

        // 获取gen ID
        return currentId.incrementAndGet();

    }


    /**
     * 当前segment的下标
     */
    private SegmentIndexEnum currentIndex() {
        return segmentIndexEnum;

    }

    /**
     * 切换索引后的下标
     */
    private SegmentIndexEnum swappedIndex() {
        return SegmentIndexEnum.getByCode(!segmentIndexEnum.isCode());
    }

    /**
     * 切换segment
     */
    private void swapSegment() {
        this.segmentIndexEnum = this.swappedIndex();
    }

    /**
     * 是否加载完成下一个segment， true:未完成  false:已完成
     */
    private boolean isNotLoadOfNextSegment() {
        if (segment[swappedIndex().getIndex()] == null) {
            return true;
        }
        if (segment[swappedIndex().getIndex()].getMinId() < segment[currentIndex().getIndex()].getMinId()) {
            return true;
        }
        return false;
    }


    private IdSegment doUpdateNextSegment(String bizTag) {
        return updateId(bizTag);
    }

    private IdSegment updateId(String bizTag) {
        IdSegmentEntity idSegmentEntity = idSegmentEntityMapper.selectByBizTag(bizTag);

        IdSegmentEntity update = new IdSegmentEntity();
        update.setBizTag(idSegmentEntity.getBizTag());
        update.setOldMaxId(idSegmentEntity.getMaxId());
        update.setMaxId(idSegmentEntity.getMaxId() + idSegmentEntity.getPStep());
        update.setLastUpdateTime(new Date());
        int row = idSegmentEntityMapper.updateByBizTagAndMaxId(update);

        if (row == 1) {
            IdSegment newSegment = new IdSegment();
            newSegment.setStep(idSegmentEntity.getPStep());
            newSegment.setMaxId(idSegmentEntity.getMaxId());
            log.info("获取分布式id号段:bizTag={},maxId={}", bizTag, idSegmentEntity.getMaxId());
            return newSegment;
        } else {
            // 直到成功为止，失败后无限重试
            return updateId(bizTag);
        }

    }


    @Data
    private static class IdSegment {

        private Long minId;
        private Long maxId;

        private Long step;

        private Long middleId;

        private Date lastUpdateTime;
        private Date currentUpdateTime;

        public IdSegment() {
        }

        public Long getMiddleId() {
            if (this.middleId == null) {
                this.middleId = this.maxId - (long) Math.round(step / 2);
            }
            return middleId;
        }

        public Long getMinId() {
            if (this.minId == null) {
                if (this.maxId != null && this.step != null) {
                    this.minId = this.maxId - this.step;
                } else {
                    throw new RuntimeException("maxid or step is null");
                }
            }

            return minId;
        }

        @Override
        public String toString() {
            return "IdSegment [minId=" + minId + ", maxId=" + maxId + ", step=" + step + ", middleId=" + middleId
                    + ", lastUpdateTime=" + lastUpdateTime + ", currentUpdateTime=" + currentUpdateTime + "]";
        }
    }


    @Getter
    private enum SegmentIndexEnum {
        first(false, 0),
        SECOND(true, 1);
        private final boolean code;
        private final int index;

        SegmentIndexEnum(boolean code, int index) {
            this.code = code;
            this.index = index;
        }

        public static SegmentIndexEnum getByCode(boolean code) {
            return Arrays.stream(SegmentIndexEnum.values())
                    .filter(item -> code == item.code)
                    .findFirst()
                    .orElse(null);
        }

    }

}
