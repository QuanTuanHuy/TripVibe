package huy.project.inventory_service.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Multi-level caching implementation inspired by Netflix EVCache and Booking.com architecture
 *
 * Cache levels:
 * 1. L1 - Local Caffeine Cache (ultra-fast, instance-specific)
 * 2. L2 - Redis Distributed Cache (shared across all service instances)
 */
@Slf4j
public class MultiLevelCache<K, V> {

    private final Cache<K, Optional<V>> localCache;
    private final RedisTemplate<K, V> redisTemplate;
    private final String region;
    private final Duration localExpiration;
    private final Duration redisExpiration;

    /**
     * Creates a new multi-level cache
     * 
     * @param redisTemplate Redis template for distributed cache
     * @param region Cache region name
     * @param localSize Maximum size of local cache
     * @param localExpiration Expiration time for local cache
     * @param redisExpiration Expiration time for redis cache
     */
    public MultiLevelCache(
            RedisTemplate<K, V> redisTemplate, 
            String region, 
            int localSize,
            Duration localExpiration,
            Duration redisExpiration) {
        
        this.redisTemplate = redisTemplate;
        this.region = region;
        this.localExpiration = localExpiration;
        this.redisExpiration = redisExpiration;
        
        // Configure local cache with Netflix-style settings
        this.localCache = Caffeine.newBuilder()
                .maximumSize(localSize)
                .expireAfterWrite(localExpiration)
                .recordStats()
                .build();
    }

    /**
     * Get value from cache, using provided supplier if not found
     * Uses cache-aside pattern with read-through caching
     */
    public V get(K key, Function<K, V> supplier) {
        String cacheKey = formatKey(key);
        
        // Try L1 cache first (fastest)
        Optional<V> localValue = localCache.getIfPresent(key);
        
        if (localValue != null) {
            log.debug("L1 cache hit for key: {}", cacheKey);
            return localValue.orElse(null);
        }
        
        // Try L2 cache next
        V redisValue = redisTemplate.opsForValue().get(key);
        
        if (redisValue != null) {
            log.debug("L2 cache hit for key: {}", cacheKey);
            // Populate L1 cache for future requests
            localCache.put(key, Optional.ofNullable(redisValue));
            return redisValue;
        }
        
        // Cache miss - call supplier and populate cache
        log.debug("Cache miss for key: {}", cacheKey);
        V value = supplier.apply(key);
        
        // Using Optional to cache null values (important for negative caching)
        localCache.put(key, Optional.ofNullable(value));
        
        if (value != null) {
            redisTemplate.opsForValue().set(key, value, redisExpiration.getSeconds(), TimeUnit.SECONDS);
        }
        
        return value;
    }

    /**
     * Put value in both cache levels
     */
    public void put(K key, V value) {
        String cacheKey = formatKey(key);
        log.debug("Caching value for key: {}", cacheKey);
        
        localCache.put(key, Optional.ofNullable(value));
        
        if (value != null) {
            redisTemplate.opsForValue().set(key, value, redisExpiration.getSeconds(), TimeUnit.SECONDS);
        }
    }

    /**
     * Invalidate entry from both cache levels
     */
    public void invalidate(K key) {
        String cacheKey = formatKey(key);
        log.debug("Invalidating cache for key: {}", cacheKey);
        
        localCache.invalidate(key);
        redisTemplate.delete(key);
    }

    /**
     * Format cache key with region
     */
    private String formatKey(K key) {
        return region + ":" + key;
    }
}
