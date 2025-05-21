package huy.project.inventory_service.ui.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import huy.project.inventory_service.infrastructure.cache.NearCache;
import huy.project.inventory_service.kernel.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for monitoring cache metrics in real-time
 * Similar to Netflix's EVCache dashboard
 */
@RestController
@RequestMapping("/api/v1/internal/cache")
@RequiredArgsConstructor
public class CacheDashboardController {

    private final CacheManager cacheManager;
    private final NearCache nearCache;
    
    /**
     * Get cache statistics for all caches
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Get stats for each cache
        cacheManager.getCacheNames().forEach(name -> {
            CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache(name);
            if (caffeineCache != null) {
                Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                CacheStats cacheStats = nativeCache.stats();
                
                Map<String, Object> cacheMetrics = new HashMap<>();
                cacheMetrics.put("size", nativeCache.estimatedSize());
                cacheMetrics.put("hitCount", cacheStats.hitCount());
                cacheMetrics.put("missCount", cacheStats.missCount());
                cacheMetrics.put("hitRate", cacheStats.hitRate());
                cacheMetrics.put("missRate", 1.0 - cacheStats.hitRate());
                cacheMetrics.put("loadSuccessCount", cacheStats.loadSuccessCount());
                cacheMetrics.put("loadFailureCount", cacheStats.loadFailureCount());
                cacheMetrics.put("totalLoadTime", cacheStats.totalLoadTime());
                cacheMetrics.put("evictionCount", cacheStats.evictionCount());
                cacheMetrics.put("evictionWeight", cacheStats.evictionWeight());
                
                stats.put(name, cacheMetrics);
            }
        });
        
        // Add Redis health status
        stats.put("redisStatus", nearCache.isRedisHealthy() ? "healthy" : "unhealthy");
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Clear all caches (admin operation)
     */
    @GetMapping("/clear")
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            org.springframework.cache.Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
        
        return ResponseEntity.ok(Map.of("status", "All caches cleared"));
    }
    
    /**
     * Get cache health information
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getCacheHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // Add local cache info
        Map<String, Object> localCacheInfo = new HashMap<>();
        localCacheInfo.put("status", "healthy");
        localCacheInfo.put("cacheCount", cacheManager.getCacheNames().size());
        
        // Add Redis cache info
        Map<String, Object> redisCacheInfo = new HashMap<>();
        boolean redisHealthy = nearCache.isRedisHealthy();
        redisCacheInfo.put("status", redisHealthy ? "healthy" : "unhealthy");
        
        health.put("localCache", localCacheInfo);
        health.put("redisCache", redisCacheInfo);
        health.put("overall", redisHealthy ? "healthy" : "degraded");
        
        return ResponseEntity.ok(health);
    }
}
