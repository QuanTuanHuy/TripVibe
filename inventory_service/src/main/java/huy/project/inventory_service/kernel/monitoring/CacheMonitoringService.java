package huy.project.inventory_service.kernel.monitoring;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Cache monitoring service
 * Similar to Netflix's EVCache monitoring system
 */
@Component
public class CacheMonitoringService {

    private final CacheManager cacheManager;
    private final MeterRegistry meterRegistry;

    public CacheMonitoringService(CacheManager cacheManager, MeterRegistry meterRegistry) {
        this.cacheManager = cacheManager;
        this.meterRegistry = meterRegistry;
        
        // Register cache metrics with Micrometer
        registerCacheMetrics();
    }

    private void registerCacheMetrics() {
        cacheManager.getCacheNames().forEach(name -> {
            CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache(name);
            if (caffeineCache != null) {
                CaffeineCacheMetrics.monitor(
                        meterRegistry,
                        caffeineCache.getNativeCache(),
                        name,
                        Collections.singletonList(Tag.of("cacheType", "local"))
                );
            }
        });
    }

    /**
     * Log cache statistics periodically
     * This helps identify cache efficiency and potential problems
     */
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void logCacheStatistics() {
        cacheManager.getCacheNames().forEach(name -> {
            CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache(name);
            if (caffeineCache != null) {
                CacheStats stats = caffeineCache.getNativeCache().stats();
                
                // Calculate metrics
                long requestCount = stats.requestCount();
                long hitCount = stats.hitCount();
                double hitRate = stats.hitRate();
                double missRate = 1.0 - hitRate;
                
                // Log metrics
                System.out.printf(
                        "Cache %s - Size: %d, Hits: %d, Requests: %d, Hit Rate: %.2f%%, Miss Rate: %.2f%%, Evictions: %d%n",
                        name,
                        caffeineCache.getNativeCache().estimatedSize(),
                        hitCount,
                        requestCount,
                        hitRate * 100,
                        missRate * 100,
                        stats.evictionCount()
                );
            }
        });
    }
}
