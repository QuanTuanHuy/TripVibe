//package huy.project.profile_service.infrastructure.cache;
//
//import huy.project.profile_service.core.domain.entity.TouristEntity;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionPhase;
//import org.springframework.transaction.event.TransactionalEventListener;
//
///**
// * Cache invalidation strategy based on domain events
// * Ensures cache consistency when data changes
// */
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class CacheInvalidationHandler {
//
//    private final MultiLevelCacheManager cacheManager;
//
//    /**
//     * Invalidate cache when tourist data is updated
//     */
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void handleTouristUpdated(TouristUpdatedEvent event) {
//        try {
//            log.debug("Invalidating cache for updated tourist: {}", event.getTouristId());
//
//            // Get the tourist cache and invalidate specific entry
//            MultiLevelCache<String, TouristEntity> touristCache =
//                cacheManager.getCache("profile_TouristEntity");
//
//            if (touristCache != null) {
//                String cacheKey = String.valueOf(event.getTouristId());
//                touristCache.invalidate(cacheKey);
//
//                // If the tourist has a new profile, warm it up immediately
//                if (event.getUpdatedTourist() != null) {
//                    touristCache.put(cacheKey, event.getUpdatedTourist());
//                }
//            }
//        } catch (Exception e) {
//            log.error("Failed to invalidate cache for tourist: {}", event.getTouristId(), e);
//        }
//    }
//
//    /**
//     * Invalidate cache when tourist is deleted
//     */
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void handleTouristDeleted(TouristDeletedEvent event) {
//        try {
//            log.debug("Invalidating cache for deleted tourist: {}", event.getTouristId());
//
//            MultiLevelCache<String, TouristEntity> touristCache =
//                cacheManager.getCache("profile_TouristEntity");
//
//            if (touristCache != null) {
//                touristCache.invalidate(String.valueOf(event.getTouristId()));
//            }
//        } catch (Exception e) {
//            log.error("Failed to invalidate cache for deleted tourist: {}", event.getTouristId(), e);
//        }
//    }
//
//    /**
//     * Warm up cache when tourist is created
//     */
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void handleTouristCreated(TouristCreatedEvent event) {
//        try {
//            log.debug("Warming cache for new tourist: {}", event.getTourist().getId());
//
//            MultiLevelCache<String, TouristEntity> touristCache =
//                cacheManager.getCache("profile_TouristEntity");
//
//            if (touristCache != null) {
//                touristCache.put(String.valueOf(event.getTourist().getId()), event.getTourist());
//            }
//        } catch (Exception e) {
//            log.error("Failed to warm cache for new tourist: {}", event.getTourist().getId(), e);
//        }
//    }
//
//    // Domain Events
//    public static class TouristUpdatedEvent {
//        private final Long touristId;
//        private final TouristEntity updatedTourist;
//
//        public TouristUpdatedEvent(Long touristId, TouristEntity updatedTourist) {
//            this.touristId = touristId;
//            this.updatedTourist = updatedTourist;
//        }
//
//        public Long getTouristId() {
//            return touristId;
//        }
//
//        public TouristEntity getUpdatedTourist() {
//            return updatedTourist;
//        }
//    }
//
//    public static class TouristDeletedEvent {
//        private final Long touristId;
//
//        public TouristDeletedEvent(Long touristId) {
//            this.touristId = touristId;
//        }
//
//        public Long getTouristId() {
//            return touristId;
//        }
//    }
//
//    public static class TouristCreatedEvent {
//        private final TouristEntity tourist;
//
//        public TouristCreatedEvent(TouristEntity tourist) {
//            this.tourist = tourist;
//        }
//
//        public TouristEntity getTourist() {
//            return tourist;
//        }
//    }
//}
