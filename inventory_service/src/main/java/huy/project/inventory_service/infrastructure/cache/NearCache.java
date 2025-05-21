package huy.project.inventory_service.infrastructure.cache;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Implementation of the Near Cache pattern with circuit breaking
 * Similar to Netflix's EVCache pattern
 * 
 * Near Cache combines local and distributed caching with
 * smart fallback and failure detection
 */
@Component
@Slf4j
public class NearCache {
    private final RedisTemplate<String, Object> redisTemplate;
    private final CircuitBreaker circuitBreaker;
    
    public NearCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        
        // Configure circuit breaker for Redis operations
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .permittedNumberOfCallsInHalfOpenState(5)
                .slidingWindowSize(10)
                .recordExceptions(Exception.class)
                .build();
            
        this.circuitBreaker = CircuitBreaker.of("redis-circuit-breaker", config);
    }
    
    /**
     * Get a value from Redis with circuit breaking protection
     */
    public <T> T getWithCircuitBreaker(String key, Class<T> clazz, Supplier<T> fallback) {
        try {
            // Try to get from Redis using circuit breaker for protection
            return circuitBreaker.executeSupplier(() -> {
                @SuppressWarnings("unchecked")
                T value = (T) redisTemplate.opsForValue().get(key);
                return value;
            });
        } catch (Exception e) {
            // Log the error
            log.error("Redis operation failed for key '{}', using fallback: {}", key, e.getMessage());
            
            // Return the fallback value
            return fallback.get();
        }
    }
    
    /**
     * Set a value in Redis with circuit breaking protection
     */
    public boolean setWithCircuitBreaker(String key, Object value, long ttl, TimeUnit timeUnit) {
        try {
            return circuitBreaker.executeSupplier(() -> {
                redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
                return true;
            });
        } catch (Exception e) {
            log.error("Failed to set Redis key '{}': {}", key, e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a key from Redis with circuit breaking protection
     */
    public boolean deleteWithCircuitBreaker(String key) {
        try {
            return circuitBreaker.executeSupplier(() -> 
                redisTemplate.delete(key) != null
            );
        } catch (Exception e) {
            log.error("Failed to delete Redis key '{}': {}", key, e.getMessage());
            return false;
        }
    }
    
    /**
     * Check health of Redis
     */
    public boolean isRedisHealthy() {
        try {
            return circuitBreaker.executeSupplier(() -> {
                redisTemplate.opsForValue().get("health-check");
                return true;
            });
        } catch (Exception e) {
            log.error("Redis health check failed: {}", e.getMessage());
            return false;
        }
    }
}
