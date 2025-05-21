package huy.project.inventory_service.infrastructure.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Factory for creating multi-level caches with different configurations
 * Similar to Netflix's EVCache instance factory
 */
@Component
public class MultiLevelCacheFactory {

    private final RedisTemplate<Object, Object> redisTemplate;    /**
     * Constructor that accepts the generic RedisTemplate.
     * Spring will autowire the RedisTemplate<Object, Object> bean
     * that we defined in RedisConfig.
     */
    public MultiLevelCacheFactory(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Create a new cache with specified configurations
     *
     * @param region          Cache region/name
     * @param localSize       Maximum size of local cache
     * @param localExpiration Local cache expiration time
     * @param redisExpiration Redis cache expiration time
     * @return A new multi-level cache instance
     */
    @SuppressWarnings("unchecked")
    public <K, V> MultiLevelCache<K, V> createCache(
            String region,
            int localSize,
            Duration localExpiration,
            Duration redisExpiration) {

        return new MultiLevelCache<>(
                (RedisTemplate<K, V>) redisTemplate,
                region,
                localSize,
                localExpiration,
                redisExpiration);
    }

    /**
     * Create a cache for frequently accessed reference data (longer TTL)
     */
    public <K, V> MultiLevelCache<K, V> createReferenceCache(String region) {
        return createCache(
                region,
                1000,                    // Local size
                Duration.ofHours(1),     // Local TTL
                Duration.ofHours(2));    // Redis TTL
    }

    /**
     * Create a cache for transactional data (shorter TTL)
     */
    public <K, V> MultiLevelCache<K, V> createTransactionalCache(String region) {
        return createCache(
                region,
                500,                     // Local size
                Duration.ofMinutes(5),   // Local TTL
                Duration.ofMinutes(10)); // Redis TTL
    }

    /**
     * Create a cache for volatile data (very short TTL)
     */
    public <K, V> MultiLevelCache<K, V> createVolatileCache(String region) {
        return createCache(
                region,
                200,                     // Local size
                Duration.ofSeconds(30),  // Local TTL
                Duration.ofMinutes(1));  // Redis TTL
    }
}
