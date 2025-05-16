package huy.project.inventory_service.infrastructure.redis;

import huy.project.inventory_service.core.port.ILockPort;
import huy.project.inventory_service.kernel.property.RedisLockProperty;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisLock implements ILockPort {
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "   return redis.call('del', KEYS[1]) " +
                    "else " +
                    "   return 0 " +
                    "end";

    private final RedisTemplate<String, String> redisTemplate;
    private static String LOCK_NAMESPACE;
    private final ThreadLocal<String> lockValueThreadLocal;
    private final DefaultRedisScript<Long> unlockScript;
    private static long DEFAULT_LEASE_TIME;

    public RedisLock(RedisTemplate<String, String> redisTemplate, RedisLockProperty redisLockProperty) {
        LOCK_NAMESPACE = redisLockProperty.getNamespace();
        DEFAULT_LEASE_TIME = redisLockProperty.getExpiration();
        this.lockValueThreadLocal = new ThreadLocal<>();
        this.redisTemplate = redisTemplate;
        this.unlockScript = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
    }

    @Override
    public void acquireLock(String lockId) {
        String key = parseLockId(lockId);
        String value = UUID.randomUUID().toString();

        boolean acquire = false;
        while (!acquire) {
            acquire = doLock(key, value, DEFAULT_LEASE_TIME, TimeUnit.MILLISECONDS);
            if (!acquire) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while waiting for lock", e);
                }
            }
        }

        lockValueThreadLocal.set(value);
    }

    @Override
    public boolean acquireLock(String lockId, long timeToTry, TimeUnit unit) {
        String key = parseLockId(lockId);
        String value = UUID.randomUUID().toString();

        try {
            long startTime = System.currentTimeMillis();
            long timeoutMillis = unit.toMillis(timeToTry);
            long waitTime = 100;

            while (System.currentTimeMillis() - startTime < timeoutMillis) {
                if (doLock(key, value, DEFAULT_LEASE_TIME, TimeUnit.MILLISECONDS)) {
                    lockValueThreadLocal.set(value);
                    return true;
                }

                try {
                    Thread.sleep(Math.min(waitTime, timeoutMillis - (System.currentTimeMillis() - startTime)));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            handleAcquireLockFailure(lockId, e);
            return false;
        }
    }

    @Override
    public boolean acquireLock(String lockId, long timeToTry, long leaseTime, TimeUnit unit) {
        String key = parseLockId(lockId);
        String value = UUID.randomUUID().toString();

        try {
            long startTime = System.currentTimeMillis();
            long timeoutMillis = unit.toMillis(timeToTry);
            long waitTime = 100;

            while (System.currentTimeMillis() - startTime < timeoutMillis) {
                if (doLock(key, value, leaseTime, unit)) {
                    lockValueThreadLocal.set(value);
                    return true;
                }

                try {
                    Thread.sleep(Math.min(waitTime, timeoutMillis - (System.currentTimeMillis() - startTime)));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            handleAcquireLockFailure(lockId, e);
            return false;
        }
    }

    @Override
    public void releaseLock(String lockId) {
        String key = parseLockId(lockId);
        String value = lockValueThreadLocal.get();

        if (value != null) {
            try {
                Long result = redisTemplate.execute(
                        unlockScript,
                        Collections.singletonList(key),
                        value
                );
                if (result == 0) {
                    log.warn("Failed to release lock for id: {}, it might be expiry", lockId);
                }
            } catch (Exception e) {
                log.warn("Failed to release lock for id: {}", lockId, e);
            } finally {
                lockValueThreadLocal.remove();
            }
        }
    }

    @Override
    public void deleteLock(String lockId) {
        String key = parseLockId(lockId);
        redisTemplate.delete(key);
    }

    private String parseLockId(String lockId) {
        if (StringUtils.isEmpty(lockId)) {
            throw new IllegalArgumentException("Lock id must not be null or empty");
        }
        return LOCK_NAMESPACE + "." + lockId;
    }

    private boolean doLock(final String key, final String value, final long leaseTime, final TimeUnit unit) {
        try {
            Boolean result = redisTemplate.opsForValue()
                    .setIfAbsent(key, value, Duration.ofMillis(unit.toMillis(leaseTime)));
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error when try to acquire lock for key {}", key, e);
            return false;
        }
    }

    private void handleAcquireLockFailure(String lockId, Exception e) {
        log.error("Failed to acquire lock for id: {}", lockId, e);
    }
}
