package huy.project.profile_service.ui.controller.management;

import huy.project.profile_service.infrastructure.cache.MultiLevelCache;
import huy.project.profile_service.infrastructure.cache.MultiLevelCacheManager;
import huy.project.profile_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Management endpoints for cache monitoring and control
 * Only enabled in dev/staging environments for security
 */
@RestController
@RequestMapping("/api/management/v1/cache")
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.management.endpoints.enabled", havingValue = "true", matchIfMissing = false)
public class CacheManagementController {

    private final MultiLevelCacheManager cacheManager;

    /**
     * Get cache statistics for all registered caches
     */
    @GetMapping("/statistics")
    public ResponseEntity<Resource<Map<String, MultiLevelCache.CacheStatistics>>> getCacheStatistics() {
        Map<String, MultiLevelCache.CacheStatistics> statistics = cacheManager.getAllStatistics();
        return ResponseEntity.ok(new Resource<>(statistics));
    }

    /**
     * Get overall cache health status
     */
    @GetMapping("/health")
    public ResponseEntity<Resource<MultiLevelCacheManager.CacheHealthStatus>> getCacheHealth() {
        MultiLevelCacheManager.CacheHealthStatus health = cacheManager.getHealthStatus();
        return ResponseEntity.ok(new Resource<>(health));
    }

    /**
     * Get statistics for a specific cache
     */
    @GetMapping("/statistics/{cacheName}")
    public ResponseEntity<Resource<MultiLevelCache.CacheStatistics>> getCacheStatistics(@PathVariable String cacheName) {
        MultiLevelCache<?, ?> cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }
        
        MultiLevelCache.CacheStatistics statistics = cache.getStatistics();
        return ResponseEntity.ok(new Resource<>(statistics));
    }

    /**
     * Clear all caches (use with caution!)
     */
    @PostMapping("/clear-all")
    public ResponseEntity<Resource<String>> clearAllCaches() {
        log.warn("Cache clear-all operation triggered via management endpoint");
        cacheManager.clearAllCaches();
        return ResponseEntity.ok(new Resource<>("All caches cleared successfully"));
    }

    /**
     * Clear a specific cache
     */
    @PostMapping("/clear/{cacheName}")
    public ResponseEntity<Resource<String>> clearCache(@PathVariable String cacheName) {
        MultiLevelCache<?, ?> cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }
        
        log.warn("Cache clear operation triggered for cache: {}", cacheName);
        cache.clear();
        return ResponseEntity.ok(new Resource<>("Cache " + cacheName + " cleared successfully"));
    }

    /**
     * Warm up a specific cache (if supported)
     */
    @PostMapping("/warmup/{cacheName}")
    public ResponseEntity<Resource<String>> warmUpCache(@PathVariable String cacheName) {
        MultiLevelCache<?, ?> cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Note: Warmup logic would be specific to each cache type
        // This is just a placeholder for demonstration
        log.info("Cache warmup requested for cache: {}", cacheName);
        return ResponseEntity.ok(new Resource<>("Cache warmup initiated for " + cacheName));
    }

    /**
     * Get cache configuration info
     */
    @GetMapping("/info")
    public ResponseEntity<Resource<CacheInfo>> getCacheInfo() {
        Map<String, MultiLevelCache.CacheStatistics> allStats = cacheManager.getAllStatistics();
        
        CacheInfo info = CacheInfo.builder()
                .totalCaches(allStats.size())
                .cacheNames(allStats.keySet().toArray(new String[0]))
                .build();
        
        return ResponseEntity.ok(new Resource<>(info));
    }

    @lombok.Builder
    @lombok.Data
    public static class CacheInfo {
        private final int totalCaches;
        private final String[] cacheNames;
    }
}
