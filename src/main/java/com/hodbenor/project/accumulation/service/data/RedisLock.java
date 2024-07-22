package com.hodbenor.project.accumulation.service.data;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.concurrent.TimeUnit;

public class RedisLock {

    private static final String LOCK_KEY_PREFIX = "lock:";
    private static final long LOCK_EXPIRE_SEC = 10;
    private static final long timeoutMillis = TimeUnit.SECONDS.toMillis(100);

    private final Jedis jedis;
    private final String lockKey;
    private final String lockValue;

    public RedisLock(Jedis jedis, String lockKey) {
        this.jedis = jedis;
        this.lockKey = LOCK_KEY_PREFIX + lockKey;
        this.lockValue = Thread.currentThread().getName() + "-" + System.currentTimeMillis();
    }

    public boolean acquireLock() {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMillis) {
            boolean isLocked = jedis.set(lockKey, lockValue, new SetParams().nx().ex(LOCK_EXPIRE_SEC)).equals("OK");
            if (isLocked) {
                return true;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }

    public void releaseLock() {
        String currentLockValue = jedis.get(lockKey);
        if (currentLockValue != null && currentLockValue.equals(lockValue)) {
            jedis.del(lockKey);
        }
    }
}
