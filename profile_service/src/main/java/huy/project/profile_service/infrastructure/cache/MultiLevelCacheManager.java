package huy.project.profile_service.infrastructure.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Central cache manager inspired by Netflix EVCache architecture
 * Provides monitoring, health checks, and centralized management of all cache instances
 */
@Component
@Slf4j
public class MultiLevelCacheManager {
    
    private final ConcurrentHashMap<String, MultiLevelCache<?, ?>> cacheInstances = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    public MultiLevelCacheManager() {
        // Schedule periodic health checks and statistics logging
        scheduler.scheduleWithFixedDelay(this::logCacheStatistics, 1, 5, TimeUnit.MINUTES);
        scheduler.scheduleWithFixedDelay(this::performHealthChecks, 30, 30, TimeUnit.SECONDS);
    }
    
    /**
     * Register a cache instance for monitoring
     */
    public <K, V> void registerCache(String name, MultiLevelCache<K, V> cache) {
        cacheInstances.put(name, cache);
        log.info("Registered cache instance: {}", name);
    }
    
    /**
     * Unregister a cache instance
     */
    public void unregisterCache(String name) {
        MultiLevelCache<?, ?> removed = cacheInstances.remove(name);
        if (removed != null) {
            log.info("Unregistered cache instance: {}", name);
        }
    }
    
    /**
     * Get cache instance by name
     */
    @SuppressWarnings("unchecked")
    public <K, V> MultiLevelCache<K, V> getCache(String name) {
        return (MultiLevelCache<K, V>) cacheInstances.get(name);
    }
    
    /**
     * Get all cache statistics
     */
    public Map<String, MultiLevelCache.CacheStatistics> getAllStatistics() {
        return cacheInstances.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getStatistics()
                ));
    }
    
    /**
     * Clear all caches
     */
    public void clearAllCaches() {
        log.warn("Clearing all cache instances");
        cacheInstances.values().forEach(MultiLevelCache::clear);
    }
    
    /**
     * Get cache health status
     */
    public CacheHealthStatus getHealthStatus() {
        Map<String, MultiLevelCache.CacheStatistics> allStats = getAllStatistics();
        
        boolean isHealthy = true;
        double totalHitRate = 0.0;
        long totalRequests = 0;
        long totalExceptions = 0;
        
        for (Map.Entry<String, MultiLevelCache.CacheStatistics> entry : allStats.entrySet()) {
            MultiLevelCache.CacheStatistics stats = entry.getValue();
            
            // Consider cache unhealthy if hit rate is too low or too many exceptions
            if (stats.getTotalHitRate() < 0.5 || stats.getLoadExceptions() > 100) {
                isHealthy = false;
                log.warn("Cache {} shows poor performance: hitRate={}, exceptions={}", 
                        entry.getKey(), stats.getTotalHitRate(), stats.getLoadExceptions());
            }
            
            long requests = stats.getLocalHits() + stats.getRedisHits() + stats.getCacheMisses();
            totalRequests += requests;
            totalHitRate += stats.getTotalHitRate() * requests;
            totalExceptions += stats.getLoadExceptions();
        }
        
        double overallHitRate = totalRequests > 0 ? totalHitRate / totalRequests : 0.0;
        
        return CacheHealthStatus.builder()
                .healthy(isHealthy)
                .overallHitRate(overallHitRate)
                .totalRequests(totalRequests)
                .totalExceptions(totalExceptions)
                .timestamp(LocalDateTime.now())
                .cacheCount(cacheInstances.size())
                .build();
    }
    
    /**
     * Perform periodic health checks
     */
    private void performHealthChecks() {
        try {
            CacheHealthStatus health = getHealthStatus();
            if (!health.isHealthy()) {
                log.warn("Cache health check failed: {}", health);
            }
        } catch (Exception e) {
            log.error("Error during cache health check", e);
        }
    }
    
    /**
     * Log cache statistics periodically
     */
    private void logCacheStatistics() {
        try {
            Map<String, MultiLevelCache.CacheStatistics> allStats = getAllStatistics();
            
            log.info("=== Cache Statistics Summary ===");
            for (Map.Entry<String, MultiLevelCache.CacheStatistics> entry : allStats.entrySet()) {
                MultiLevelCache.CacheStatistics stats = entry.getValue();
                log.info("Cache '{}': hitRate={:.2f}%, localHits={}, redisHits={}, misses={}, exceptions={}, size={}", 
                        entry.getKey(),
                        stats.getTotalHitRate() * 100,
                        stats.getLocalHits(),
                        stats.getRedisHits(),
                        stats.getCacheMisses(),
                        stats.getLoadExceptions(),
                        stats.getLocalCacheSize());
            }
            log.info("=== End Cache Statistics ===");
        } catch (Exception e) {
            log.error("Error logging cache statistics", e);
        }
    }
    
    /**
     * Shutdown the cache manager
     */
    public void shutdown() {
        log.info("Shutting down cache manager");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    @lombok.Builder
    @lombok.Data
    public static class CacheHealthStatus {
        private final boolean healthy;
        private final double overallHitRate;
        private final long totalRequests;
        private final long totalExceptions;
        private final LocalDateTime timestamp;
        private final int cacheCount;
    }
}
