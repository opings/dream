package zyz.free.service.client.redis;


import com.google.common.base.Joiner;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import zyz.free.service.contans.Contans;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class RedisLockClient {


    private final RedissonClient redissonClient;


    public RedisLockClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }


    public <T> T lock(String nameSpace,
                      String key,
                      Callable<T> callable,
                      long waitTime,
                      long leaseTime,
                      TimeUnit unit) {
        String lockKey = buildCacheKey(nameSpace, key);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(waitTime, leaseTime, unit);
            if (isLocked) {
                T call = callable.call();
                return call;
            } else {
                throw new RuntimeException("don't get lock");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    private String buildCacheKey(String nameSpace, String key) {
        return Joiner.on(Contans.COLON).join(nameSpace, key);
    }


}
