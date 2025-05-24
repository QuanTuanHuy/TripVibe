package huy.project.inventory_service.core.worker;

import huy.project.inventory_service.core.usecase.LockTransactionManagerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Worker that periodically checks for expired locks and releases them.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LockCleanupWorker {
    private final LockTransactionManagerUseCase lockTransactionManager;

    /**
     * Runs every 5 minutes to clean up expired locks
     */
    @Scheduled(fixedRateString = "${app.lock.cleanup.interval:300000}")
    public void cleanupExpiredLocks() {
        log.info("Starting scheduled task to clean up expired locks");
        int releasedCount = lockTransactionManager.releaseExpiredLocks();
        log.info("Completed cleaning up expired locks. Released {} locks", releasedCount);
    }
}
