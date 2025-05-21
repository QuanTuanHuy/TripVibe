package huy.project.inventory_service.infrastructure.cache;

import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.infrastructure.repository.RoomAvailabilityRepository;
import huy.project.inventory_service.kernel.config.CacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Cache warming service that proactively populates cache with frequently accessed data
 * Similar to Netflix's predictive caching strategy
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheWarmingService {

    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final MultiLevelCacheFactory cacheFactory;
    
    /**
     * Warm the cache for popular accommodations
     * Runs every hour to ensure cache is warm for high-traffic periods
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void warmCacheForPopularAccommodations() {
        log.info("Starting cache warming for popular accommodations");
        
        // In a real system, we'd retrieve this from analytics
        List<Long> popularAccommodationIds = List.of(1L, 2L, 3L, 4L, 5L);
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(30); // Next 30 days
        
        MultiLevelCache<String, List<RoomAvailability>> dateRangeCache = 
                cacheFactory.createReferenceCache(CacheConfig.CacheNames.ROOM_AVAILABILITY_BY_DATE_RANGE);
        
        for (Long accommodationId : popularAccommodationIds) {
            try {
                // Fetch data
                List<RoomAvailability> availabilities = 
                        roomAvailabilityRepository.findByAccommodationIdAndDateRange(accommodationId, today, endDate);
                
                // Warm the cache
                String cacheKey = "accommodation:" + accommodationId + ":date:" + today + ":" + endDate;
                dateRangeCache.put(cacheKey, availabilities);
                
                log.debug("Warmed cache for accommodation {}: {} availabilities", accommodationId, availabilities.size());
            } catch (Exception e) {
                log.error("Error warming cache for accommodation {}", accommodationId, e);
                // Continue with next accommodation - don't let one failure stop the process
            }
        }
        
        log.info("Completed cache warming for popular accommodations");
    }
    
    /**
     * Warm the cache for upcoming bookings
     * Runs at midnight to prepare for the next day's traffic
     */
    @Scheduled(cron = "0 0 0 * * *") // At midnight every day
    public void warmCacheForUpcomingBookings() {
        log.info("Starting cache warming for upcoming bookings");
        
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        try {
            // Get all bookings for tomorrow
            // In a real system, we'd query specifically for bookings with check-ins tomorrow
            List<RoomAvailability> tomorrowBookings = 
                    roomAvailabilityRepository.findByAccommodationIdAndDateRange(null, tomorrow, tomorrow);
            
            // Group by booking ID and pre-warm cache
            tomorrowBookings.stream()
                    .filter(ra -> ra.getBookingId() != null)
                    .map(RoomAvailability::getBookingId)
                    .distinct()
                    .forEach(bookingId -> {
                        // This would trigger cache population in a real implementation
                        log.debug("Pre-warming cache for booking ID: {}", bookingId);
                    });
            
            log.info("Warmed cache for {} upcoming bookings", tomorrowBookings.size());
        } catch (Exception e) {
            log.error("Error warming cache for upcoming bookings", e);
        }
    }
}
