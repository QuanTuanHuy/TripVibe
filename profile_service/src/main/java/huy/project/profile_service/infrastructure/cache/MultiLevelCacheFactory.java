package huy.project.profile_service.infrastructure.cache;

import huy.project.profile_service.kernel.utils.JsonUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class MultiLevelCacheFactory {

    private final RedisTemplate<String, String> redisTemplate;
    private final JsonUtils jsonUtils;
    private final MultiLevelCacheManager cacheManager;

    public MultiLevelCacheFactory(RedisTemplate<String, String> redisTemplate,
                                  JsonUtils jsonUtils,
                                  MultiLevelCacheManager cacheManager) {
        this.redisTemplate = redisTemplate;
        this.jsonUtils = jsonUtils;
        this.cacheManager = cacheManager;
    }

    /**
     * Create a new cache with specified configurations
     *
     * @param domain          Cache domain/namespace
     * @param localSize       Maximum size of local cache
     * @param localExpiration Local cache expiration time
     * @param redisExpiration Redis cache expiration time
     * @param valueType       Class type for value deserialization
     * @return A new multi-level cache instance
     */
    public <K, V> MultiLevelCache<K, V> createCache(
            String domain,
            int localSize,
            Duration localExpiration,
            Duration redisExpiration,
            Class<V> valueType) {

        MultiLevelCache<K, V> cache = new MultiLevelCache<>(
                redisTemplate,
                domain,
                localSize,
                localExpiration,
                redisExpiration,
                jsonUtils,
                valueType);

        // Register with cache manager for monitoring
        String cacheName = domain + "_" + valueType.getSimpleName();
        cacheManager.registerCache(cacheName, cache);

        return cache;
    }

    /**
     * Create a cache for frequently accessed reference data (longer TTL)
     */
    public <K, V> MultiLevelCache<K, V> createReferenceCache(String domain, Class<V> valueType) {
        return createCache(
                domain,
                1000,
                Duration.ofHours(1),
                Duration.ofHours(2),
                valueType);
    }

    /**
     * Create a cache for transactional data (shorter TTL)
     */
    public <K, V> MultiLevelCache<K, V> createTransactionalCache(String domain, Class<V> valueType) {
        return createCache(
                domain,
                500,
                Duration.ofMinutes(5),
                Duration.ofMinutes(10),
                valueType);
    }

    /**
     * Create a cache for volatile data (very short TTL)
     */
    public <K, V> MultiLevelCache<K, V> createVolatileCache(String domain, Class<V> valueType) {
        return createCache(
                domain,
                200,
                Duration.ofSeconds(30),
                Duration.ofMinutes(1),
                valueType);
    }

    /**
     * Create a cache for user session data
     */
    public <K, V> MultiLevelCache<K, V> createSessionCache(String domain, Class<V> valueType) {
        return createCache(
                domain,
                300,
                Duration.ofMinutes(15),
                Duration.ofMinutes(30),
                valueType);
    }

    /**
     * Create a cache for profile data
     */
    public <K, V> MultiLevelCache<K, V> createProfileCache(Class<V> valueType) {
        return createCache(
                "profile",
                800,
                Duration.ofMinutes(30),
                Duration.ofHours(1),
                valueType);
    }
}