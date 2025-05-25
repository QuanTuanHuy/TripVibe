//package huy.project.profile_service.infrastructure.cache;
//
//import huy.project.profile_service.core.domain.entity.TouristEntity;
//import huy.project.profile_service.core.port.ITouristPort;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//
///**
// * Cache warming service to pre-populate cache with frequently accessed data
// * Inspired by Netflix EVCache warming strategies
// */
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class CacheWarmingService {
//
//    private final MultiLevelCacheFactory cacheFactory;
//    private final ITouristPort touristPort;
//
//    /**
//     * Warm up caches after application startup
//     */
//    @EventListener(ApplicationReadyEvent.class)
//    @Async
//    public void warmUpCachesOnStartup() {
//        log.info("Starting cache warming process...");
//
//        CompletableFuture.allOf(
//                warmUpTouristCache(),
//                warmUpFrequentlyAccessedData()
//        ).thenRun(() -> {
//            log.info("Cache warming completed successfully");
//        }).exceptionally(throwable -> {
//            log.error("Cache warming failed", throwable);
//            return null;
//        });
//    }
//
//    /**
//     * Warm up tourist cache with recent active users
//     */
//    @Async
//    public CompletableFuture<Void> warmUpTouristCache() {
//        return CompletableFuture.runAsync(() -> {
//            try {
//                log.info("Warming up tourist cache...");
//
//                // Create a dedicated cache for warmup
//                MultiLevelCache<String, TouristEntity> touristCache =
//                    cacheFactory.createProfileCache(TouristEntity.class);
//
//                // Get recently active tourists (this would be implemented based on your business logic)
//                List<TouristEntity> recentTourists = getRecentlyActiveTourists();
//
//                Map<String, TouristEntity> warmUpData = new HashMap<>();
//                for (TouristEntity tourist : recentTourists) {
//                    warmUpData.put(String.valueOf(tourist.getId()), tourist);
//                }
//
//                // Warm up the cache
//                touristCache.warmUp(warmUpData);
//
//                log.info("Tourist cache warmed up with {} entries", warmUpData.size());
//            } catch (Exception e) {
//                log.error("Failed to warm up tourist cache", e);
//                throw new RuntimeException(e);
//            }
//        });
//    }
//
//    /**
//     * Warm up other frequently accessed data
//     */
//    @Async
//    public CompletableFuture<Void> warmUpFrequentlyAccessedData() {
//        return CompletableFuture.runAsync(() -> {
//            try {
//                log.info("Warming up frequently accessed data...");
//
//                // Warm up location data
//                warmUpLocationData();
//
//                // Warm up user settings
//                warmUpUserSettings();
//
//                log.info("Frequently accessed data warmed up successfully");
//            } catch (Exception e) {
//                log.error("Failed to warm up frequently accessed data", e);
//                throw new RuntimeException(e);
//            }
//        });
//    }
//
//    /**
//     * Get recently active tourists (implement based on your business logic)
//     */
//    private List<TouristEntity> getRecentlyActiveTourists() {
//        try {
//            // This should be implemented to get tourists who were active recently
//            // For example: last login within 24 hours, recent bookings, etc.
//
//            // Placeholder implementation - in real scenario, this would query
//            // the database with appropriate filters
//            return touristPort.getRecentlyActiveTourists(100); // Top 100 active users
//        } catch (Exception e) {
//            log.warn("Failed to get recently active tourists, using empty list", e);
//            return List.of();
//        }
//    }
//
//    /**
//     * Warm up location data
//     */
//    private void warmUpLocationData() {
//        try {
//            // Implement location data warming
//            // This could include popular cities, countries, etc.
//            log.debug("Location data warming placeholder");
//        } catch (Exception e) {
//            log.warn("Failed to warm up location data", e);
//        }
//    }
//
//    /**
//     * Warm up user settings
//     */
//    private void warmUpUserSettings() {
//        try {
//            // Implement user settings warming
//            // This could include default preferences, etc.
//            log.debug("User settings warming placeholder");
//        } catch (Exception e) {
//            log.warn("Failed to warm up user settings", e);
//        }
//    }
//
//    /**
//     * Manual cache warming trigger (can be called via management endpoint)
//     */
//    public void triggerManualWarmUp() {
//        log.info("Manual cache warming triggered");
//        warmUpCachesOnStartup();
//    }
//
//    /**
//     * Warm up specific tourist data
//     */
//    public void warmUpTourist(Long touristId) {
//        try {
//            TouristEntity tourist = touristPort.getTouristById(touristId);
//            if (tourist != null) {
//                MultiLevelCache<String, TouristEntity> touristCache =
//                    cacheFactory.createProfileCache(TouristEntity.class);
//                touristCache.put(String.valueOf(touristId), tourist);
//                log.debug("Warmed up cache for tourist: {}", touristId);
//            }
//        } catch (Exception e) {
//            log.warn("Failed to warm up cache for tourist: {}", touristId, e);
//        }
//    }
//}
