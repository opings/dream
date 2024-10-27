package zyz.free.service.client.redis;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import jodd.exception.ExceptionUtil;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import zyz.free.service.contans.Contans;
import zyz.free.util.AssertUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static zyz.free.service.contans.Contans.*;

@Log4j2
@Component
public class RedisCacheClient {


    private final RedissonClient redissonClient;


    public RedisCacheClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 锁cache
     */
    private Cache<String, Serializable> lockCache =
            CacheBuilder.newBuilder().maximumSize(NUM_500).expireAfterWrite(ONE, TimeUnit.SECONDS).build();
    /**
     * 热点缓存 cache
     */
    private Cache<String, Serializable> hotKeyCache =
            CacheBuilder.newBuilder().maximumSize(NUM_6000).expireAfterWrite(FIVE, TimeUnit.SECONDS).build();
    /**
     * 热点统计map
     */
    private Map<String, QpsMetric> hotKeyStatisticsMap = CacheBuilder.newBuilder().maximumSize(500).<String, QpsMetric>build().asMap();


    /**
     * 数据放入缓存
     *
     * @param nameSpace  命名空间
     * @param key        缓存key
     * @param value      缓存value
     * @param timeToLive 过期时间 秒为单位
     */
    public void set(String nameSpace, String key, Serializable value, int timeToLive) {
        AssertUtil.notBlank(nameSpace, () -> "缓存nameSpace empty");
        AssertUtil.notBlank(key, () -> "缓存key empty");
        AssertUtil.isTrue(timeToLive >= ONE, () -> "过期时间必须>=1秒");

        try {
            redissonClient.getBucket(buildCacheKey(nameSpace, key)).set(value, timeToLive, TimeUnit.SECONDS);
            log.info("RedisCacheClient.set.success. key:{}", buildCacheKey(nameSpace, key));
        } catch (Exception ex) {
            log.error("RedisCacheClient.set.error. key:{}", buildCacheKey(nameSpace, key), ex);
        }
    }


    /**
     * 从缓存获取数据
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> T get(String nameSpace, String key) {

        AssertUtil.notBlank(nameSpace, () -> "缓存nameSpace empty");
        AssertUtil.notBlank(key, () -> "缓存key empty");

        try {
            return (T) redissonClient.getBucket(buildCacheKey(nameSpace, key)).get();
        } catch (Exception ex) {
            log.error("RedisCacheClient.set.error. key:{}", buildCacheKey(nameSpace, key), ex);
        }
        return null;
    }


    /**
     * 从缓存获取数据 如果获取失败 从底层数据源回源,放入缓存
     * <ul>
     * <li>优先本地缓存</li>
     * <li>其次redis</li>
     * <li>其次回源</li>
     * <li>放入本地缓存 redis</li>
     * </ul>
     *
     * @param key
     * @param callable
     * @param timeToLive
     * @param <T>
     * @return
     */
    public <T extends Serializable> T get(String nameSpace, String key, Callable<T> callable, int timeToLive) {

        AssertUtil.notBlank(nameSpace, () -> "缓存nameSpace empty");
        AssertUtil.notBlank(key, () -> "缓存key empty");
        AssertUtil.notNull(callable, () -> "callable null");
        AssertUtil.isTrue(timeToLive >= ONE, () -> "过期时间必须>=1秒");

        String cacheKey = buildCacheKey(nameSpace, key);

        QpsMetric statisticsMetric = hotKeyStatisticsMap.computeIfAbsent(cacheKey, item -> new QpsMetric());

        T result = (T) hotKeyCache.getIfPresent(cacheKey);
        if (result == null) {
            result = innerGet(nameSpace, key, callable, timeToLive);
        }

        statisticsMetric.increase();
        if (statisticsMetric.getLastWindowQps() >= TWO) {
            log.warn("检测到热点key{} tps {}", cacheKey, statisticsMetric.getLastWindowQps());
            if (result != null) {
                hotKeyCache.put(cacheKey, result);
            }
        }
        return result;
    }


    /**
     * 从缓存获取数据 如果获取失败 从底层数据源回源,放入缓存
     * <ul>
     * <li>优先本地缓存</li>
     * <li>其次redis</li>
     * <li>其次回源</li>
     * <li>放入本地缓存 redis</li>
     * </ul>
     *
     * @param key
     * @param callable
     * @param timeToLive
     * @param <T>
     * @return
     */
    private <T extends Serializable> T innerGet(String nameSpace, String key, Callable<T> callable,
                                                int timeToLive) {

        Serializable value = get(nameSpace, key);
        if (value != null) {
            return (T) value;
        }
        value = fetchSource(nameSpace, key, callable);
        set(nameSpace, key, value, timeToLive);
        return (T) value;
    }


    /**
     * 回源<br>
     * 并发情况下只有单线程回源
     *
     * @param nameSpace
     * @param key
     * @param callable
     * @param <T>
     * @return
     */
    private <T extends Serializable> T fetchSource(String nameSpace, String key, Callable<T> callable) {

        try {
            T value = (T) lockCache.get(buildCacheKey(nameSpace, key), callable);
            return value;
        } catch (Throwable ex) {
            Throwable rootCause = ExceptionUtil.getRootCause(ex);
            if (rootCause instanceof CacheLoader.InvalidCacheLoadException) {
                return null;
            }
            throw new RuntimeException("RedisCacheClient.fetchSource.error", rootCause);
        }
    }


    private String buildCacheKey(String nameSpace, String key) {
        return Joiner.on(Contans.COLON).join(nameSpace, key);
    }


    public static class QpsMetric {
        private final AtomicLong counter = new AtomicLong(0);
        private final List<Map<String, Long>> qpsList = new ArrayList<>();

        public QpsMetric() {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                Long count = counter.getAndSet(0);
                Map<String, Long> map = new HashMap<>();
                String dateSrt = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN);
                map.put(dateSrt, count);
                qpsList.add(map);
            }, 0, 1, TimeUnit.SECONDS);
        }


        public void increase() {
            counter.addAndGet(1);
        }

        public long getLastWindowQps() {
            return counter.get();
        }

        public List<Map<String, Long>> getQpaList() {
            return qpsList;
        }

    }


}
