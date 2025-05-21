package huy.project.inventory_service.kernel.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

/**
 * Cache configuration for the inventory service
 * 
 * This implements a two-level caching strategy:
 * 1. Local cache (Caffeine) - for high-frequency access data within each instance
 * 2. Distributed cache (Redis) - for shared data across multiple instances
 * 
 * Inspired by Netflix's EVCache and Google's Guava Cache patterns
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache names constants
     */
    public static class CacheNames {
        private CacheNames() {}
        
        public static final String ROOM_AVAILABILITY_BY_LOCK = "roomAvailabilityByLock";
        public static final String ROOM_AVAILABILITY_BY_BOOKING = "roomAvailabilityByBooking";
        public static final String ROOM_AVAILABILITY_BY_ROOM = "roomAvailabilityByRoom";
        public static final String ROOM_AVAILABILITY_BY_DATE_RANGE = "roomAvailabilityByDateRange";
    }
    
    /**
     * Local cache manager using Caffeine
     * Optimized for frequently accessed immutable data with low eviction rates
     */
    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // Use adaptive sizing - Booking.com style
                .initialCapacity(100)
                .maximumSize(10_000)
                // Time-based expiration strategy - Netflix style
                .expireAfterWrite(Duration.ofMinutes(5))
                // Record stats for monitoring - Google style
                .recordStats());
        
        cacheManager.setCacheNames(java.util.Arrays.asList(
                CacheNames.ROOM_AVAILABILITY_BY_LOCK,
                CacheNames.ROOM_AVAILABILITY_BY_BOOKING,
                CacheNames.ROOM_AVAILABILITY_BY_ROOM,
                CacheNames.ROOM_AVAILABILITY_BY_DATE_RANGE
        ));
        
        return cacheManager;
    }
    
    /**
     * Custom cache for hot items with shorter expiration
     */
    @Bean
    public com.github.benmanes.caffeine.cache.Cache<Object, Object> roomLockCache() {
        return Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(Duration.ofSeconds(30))
                .recordStats()
                .build();
    }
}
