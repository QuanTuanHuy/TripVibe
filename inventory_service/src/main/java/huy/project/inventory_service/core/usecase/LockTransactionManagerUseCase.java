package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.port.ILockPort;
import huy.project.inventory_service.core.port.IRoomAvailabilityPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service responsible for managing transactional operations when locking rooms.
 * This service ensures that both Redis locks and database locks are managed consistently,
 * preventing race conditions and ensuring data integrity.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LockTransactionManagerUseCase {
    private final ILockPort lockPort;
    private final IRoomAvailabilityPort roomAvailabilityPort;

    /**
     * Releases a lock with transactional safety.
     * Updates the database first, then releases the distributed lock.
     *
     * @param lockId The unique lock identifier
     * @return True if lock was successfully released
     */
    @Transactional
    public boolean releaseLock(String lockId) {
        try {
            // Update the database first
            int updatedCount = roomAvailabilityPort.releaseLocksById(lockId);
            log.info("Released {} room availability locks for lockId: {}", updatedCount, lockId);

            // Then release the distributed lock
            lockPort.releaseLock(lockId);

            return true;
        } catch (Exception e) {
            log.error("Error while releasing lock for lockId: {}", lockId, e);
            return false;
        }
    }

    /**
     * Releases all expired locks in the system.
     * This is typically called by a scheduled task.
     *
     * @return The number of locks released
     */
    @Transactional
    public int releaseExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();
        try {
            int releasedCount = roomAvailabilityPort.releaseExpiredLocks(now);
            log.info("Released {} expired locks", releasedCount);
            return releasedCount;
        } catch (Exception e) {
            log.error("Error while releasing expired locks", e);
            return 0;
        }
    }
}
