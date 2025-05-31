package huy.project.profile_service.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import huy.project.profile_service.core.port.IMultiLevelCache;
import huy.project.profile_service.kernel.utils.JsonUtils;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public class MultiLevelCache<K, V> implements IMultiLevelCache<K, V> {

    private final Cache<String, CacheEntry<V>> localCache;
    private final RedisTemplate<String, String> redisTemplate;
    private final String domain;
    private final Duration localExpiration;
    private final Duration redisExpiration;
    private final JsonUtils jsonUtils;
    private final Class<V> valueType;
    private final ConcurrentHashMap<String, CompletableFuture<V>> loadingCache = new ConcurrentHashMap<>();

    // Cache statistics
    private volatile long localHits = 0;
    private volatile long redisHits = 0;
    private volatile long cacheMisses = 0;
    private volatile long loadExceptions = 0;

    public MultiLevelCache(RedisTemplate<String, String> redisTemplate, String domain, int localSize, Duration localExpiration, Duration redisExpiration, JsonUtils jsonUtils, Class<V> valueType) {
        this.redisTemplate = redisTemplate;
        this.jsonUtils = jsonUtils;
        this.localExpiration = localExpiration;
        this.redisExpiration = redisExpiration;
        this.domain = domain;
        this.valueType = valueType;
        this.localCache = Caffeine.newBuilder()
                .maximumSize(localSize)
                .expireAfterWrite(localExpiration)
                .recordStats()
                .removalListener((key, value, cause) -> {
                    log.debug("Local cache entry removed: key={}, cause={}", key, cause);
                })
                .build();
    }

    @Override
    public V get(K key, Function<K, V> supplier) {
        String cacheKey = formatKey(key);

        // Check if already loading to prevent cache stampede
        CompletableFuture<V> loadingFuture = loadingCache.get(cacheKey);
        if (loadingFuture != null && !loadingFuture.isDone()) {
            try {
                log.debug("Cache key {} is already being loaded, waiting...", cacheKey);
                return loadingFuture.get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("Failed to wait for loading cache key: {}", cacheKey, e);
            }
        }

        // Level 1: Local Cache
        CacheEntry<V> localEntry = localCache.getIfPresent(cacheKey);
        if (localEntry != null && !localEntry.isExpired(localExpiration)) {
            localHits++;
            log.debug("Cache hit in local for key: {}", cacheKey);

            // Return null if it's a cached null value
            if (localEntry.isNullValue()) {
                return null;
            }

            return localEntry.getValue();
        }

        // Level 2: Redis Cache
        try {
            String redisValue = redisTemplate.opsForValue().get(cacheKey);
            if (redisValue != null) {
                redisHits++;
                log.debug("Cache hit in Redis for key: {}", cacheKey);

                V deserializedValue;
                if ("__NULL__".equals(redisValue)) {
                    deserializedValue = null;
                } else {
                    deserializedValue = jsonUtils.fromJson(redisValue, valueType);
                }

                // Store in local cache
                localCache.put(cacheKey, new CacheEntry<>(deserializedValue));
                return deserializedValue;
            }
        } catch (Exception e) {
            log.warn("Failed to get value from Redis for key: {}", cacheKey, e);
        }

        // Level 3: Data Source with cache stampede protection
        cacheMisses++;
        log.debug("Cache miss for key: {}, fetching from supplier", cacheKey);

        CompletableFuture<V> future = new CompletableFuture<>();
        CompletableFuture<V> existingFuture = loadingCache.putIfAbsent(cacheKey, future);

        if (existingFuture != null) {
            // Another thread is already loading, wait for it
            try {
                return existingFuture.get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("Failed to wait for concurrent load of key: {}", cacheKey, e);
                loadExceptions++;
            }
        }

        try {
            V value = supplier.apply(key);

            // Store in both caches
            put(key, value);

            future.complete(value);
            return value;
        } catch (Exception e) {
            loadExceptions++;
            log.error("Failed to load value for key: {}", cacheKey, e);
            future.completeExceptionally(e);
            throw new RuntimeException("Failed to load cache value", e);
        } finally {
            loadingCache.remove(cacheKey);
        }
    }

    @Override
    public void put(K key, V value) {
        String cacheKey = formatKey(key);
        log.debug("Caching value for key: {}", cacheKey);

        // Store in local cache
        localCache.put(cacheKey, new CacheEntry<>(value));

        // Store in Redis cache with null handling
        try {
            String serializedValue;
            if (value == null) {
                serializedValue = "__NULL__";
            } else {
                serializedValue = jsonUtils.toJson(value);
            }

            redisTemplate.opsForValue().set(cacheKey, serializedValue, redisExpiration);
        } catch (Exception e) {
            log.warn("Failed to store value in Redis for key: {}", cacheKey, e);
        }
    }

    @Override
    public void invalidate(K key) {
        String cacheKey = formatKey(key);
        log.debug("Invalidating key: {}", cacheKey);

        // Remove from local cache
        localCache.invalidate(cacheKey);

        // Remove from Redis cache
        try {
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.warn("Failed to delete key from Redis: {}", cacheKey, e);
        }
    }

    // Internal cache entry wrapper
    @Data
    private static class CacheEntry<T> {
        private final T value;
        private final long timestamp;
        private final boolean isNullValue;

        public CacheEntry(T value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
            this.isNullValue = (value == null);
        }

        public boolean isExpired(Duration duration) {
            return System.currentTimeMillis() - timestamp > duration.toMillis();
        }

    }

    /**
     * Get cache statistics
     */
    public CacheStatistics getStatistics() {
        CacheStats localStats = localCache.stats();
        return CacheStatistics.builder()
                .localHits(localHits)
                .redisHits(redisHits)
                .cacheMisses(cacheMisses)
                .loadExceptions(loadExceptions)
                .localCacheSize(localCache.estimatedSize())
                .localEvictionCount(localStats.evictionCount())
                .localHitRate(localStats.hitRate())
                .build();
    }

    /**
     * Clear all cache levels
     */
    public void clear() {
        log.info("Clearing all cache levels for domain: {}", domain);

        // Clear local cache
        localCache.invalidateAll();

        // Clear Redis cache by pattern (be careful with this in production)
        try {
            String pattern = "profile_service:" + domain + ":*";
            // Note: Using SCAN pattern would be better for large datasets
            redisTemplate.delete(redisTemplate.keys(pattern));
        } catch (Exception e) {
            log.warn("Failed to clear Redis cache for domain: {}", domain, e);
        }
    }

    /**
     * Warm up cache with pre-computed values
     */
    public void warmUp(java.util.Map<K, V> warmUpData) {
        log.info("Warming up cache with {} entries for domain: {}", warmUpData.size(), domain);

        warmUpData.forEach(this::put);
    }

    /**
     * Get cache entry with metadata
     */
    public CacheEntryInfo<V> getWithInfo(K key) {
        String cacheKey = formatKey(key);

        // Check local cache first
        CacheEntry<V> localEntry = localCache.getIfPresent(cacheKey);
        if (localEntry != null && !localEntry.isExpired(localExpiration)) {
            return CacheEntryInfo.<V>builder()
                    .value(localEntry.getValue())
                    .hitLevel(CacheLevel.LOCAL)
                    .timestamp(localEntry.timestamp)
                    .build();
        }

        // Check Redis cache
        try {
            String redisValue = redisTemplate.opsForValue().get(cacheKey);
            if (redisValue != null) {
                V deserializedValue;
                if ("__NULL__".equals(redisValue)) {
                    deserializedValue = null;
                } else {
                    deserializedValue = jsonUtils.fromJson(redisValue, valueType);
                }

                // Store in local cache
                localCache.put(cacheKey, new CacheEntry<>(deserializedValue));

                return CacheEntryInfo.<V>builder()
                        .value(deserializedValue)
                        .hitLevel(CacheLevel.REDIS)
                        .timestamp(System.currentTimeMillis())
                        .build();
            }
        } catch (Exception e) {
            log.warn("Failed to get value from Redis for key: {}", cacheKey, e);
        }

        return null;
    }

    private String formatKey(K key) {
        return "profile_service:" + domain + ":" + key.toString();
    }

    // Supporting classes
    public enum CacheLevel {
        LOCAL, REDIS, MISS
    }

    @Builder
    @Data
    public static class CacheStatistics {
        private final long localHits;
        private final long redisHits;
        private final long cacheMisses;
        private final long loadExceptions;
        private final long localCacheSize;
        private final long localEvictionCount;
        private final double localHitRate;

        public double getTotalHitRate() {
            long totalRequests = localHits + redisHits + cacheMisses;
            return totalRequests > 0 ? (double) (localHits + redisHits) / totalRequests : 0.0;
        }
    }

    @Builder
    @Data
    public static class CacheEntryInfo<T> {
        private final T value;
        private final CacheLevel hitLevel;
        private final long timestamp;
    }
}
