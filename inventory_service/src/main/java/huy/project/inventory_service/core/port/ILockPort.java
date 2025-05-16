package huy.project.inventory_service.core.port;

import java.util.concurrent.TimeUnit;

public interface ILockPort {
    void acquireLock(String lockId);

    boolean acquireLock(String lockId, long timeToTry, TimeUnit unit);

    boolean acquireLock(String lockId, long timeToTry, long leaseTime, TimeUnit unit);

    void releaseLock(String lockId);

    void deleteLock(String lockId);
}
