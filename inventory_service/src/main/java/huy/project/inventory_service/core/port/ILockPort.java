package huy.project.inventory_service.core.port;

import java.util.concurrent.TimeUnit;

/**
 * Interface for distributed lock operations.
 * Provides methods to lock and unlock distributed resources.
 */
public interface ILockPort {
    /**
     * Acquires a lock with blocking behavior. This will wait indefinitely until the lock is acquired.
     * 
     * @param lockId the unique lock identifier
     */
    void acquireLock(String lockId);

    /**
     * Attempts to acquire a lock with a timeout.
     * 
     * @param lockId the unique lock identifier
     * @param timeToTry how long to try to acquire the lock
     * @param unit time unit for timeToTry
     * @return true if the lock was acquired, false otherwise
     */
    boolean acquireLock(String lockId, long timeToTry, TimeUnit unit);

    /**
     * Attempts to acquire a lock with a timeout and specific lease time.
     * 
     * @param lockId the unique lock identifier
     * @param timeToTry how long to try to acquire the lock
     * @param leaseTime how long the lock should be held
     * @param unit time unit for timeToTry and leaseTime
     * @return true if the lock was acquired, false otherwise
     */
    boolean acquireLock(String lockId, long timeToTry, long leaseTime, TimeUnit unit);


    /**
     * Releases a lock. Legacy method.
     * 
     * @param lockId the unique lock identifier
     */
    void releaseLock(String lockId);

    /**
     * Forcibly deletes a lock regardless of ownership.
     * 
     * @param lockId the unique lock identifier
     */
    void deleteLock(String lockId);
}
