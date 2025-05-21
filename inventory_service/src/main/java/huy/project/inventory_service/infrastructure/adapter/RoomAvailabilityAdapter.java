package huy.project.inventory_service.infrastructure.adapter;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.core.port.IRoomAvailabilityPort;
import huy.project.inventory_service.infrastructure.cache.MultiLevelCache;
import huy.project.inventory_service.infrastructure.cache.MultiLevelCacheFactory;
import huy.project.inventory_service.infrastructure.repository.RoomAvailabilityRepository;
import huy.project.inventory_service.kernel.config.CacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Adapter implementation for Room Availability with multi-level caching
 * Inspired by Netflix's EVCache and Booking.com's cache architecture
 */
@Component
@Slf4j
public class RoomAvailabilityAdapter implements IRoomAvailabilityPort {

    private final RoomAvailabilityRepository repository;
    private final MultiLevelCache<String, List<RoomAvailability>> lockCache;
    private final MultiLevelCache<Long, List<RoomAvailability>> bookingCache;
    private final ConcurrentHashMap<Long, Object> roomLocks = new ConcurrentHashMap<>();

    public RoomAvailabilityAdapter(
            RoomAvailabilityRepository repository,
            MultiLevelCacheFactory cacheFactory) {
        this.repository = repository;

        // Create specialized caches with different configurations
        // We use the factory to create and configure the caches - no autowiring needed for the cache itself
        this.lockCache = cacheFactory.createVolatileCache("room:lock");
        this.bookingCache = cacheFactory.createTransactionalCache("room:booking");
    }

    /**
     * Find rooms by lock ID with cacheable annotations for declarative caching
     * This uses Spring Cache with the Caffeine provider
     */
    @Override
    @Cacheable(value = CacheConfig.CacheNames.ROOM_AVAILABILITY_BY_LOCK, key = "#lockId")
    public List<RoomAvailability> findByLockId(String lockId) {
        log.debug("Cache miss for findByLockId: {}", lockId);
        return repository.findByLockId(lockId);
    }

    /**
     * Find rooms by booking ID using programmatic caching with the multi-level cache
     */
    @Override
    public List<RoomAvailability> getRoomsByBookingId(Long bookingId) {
        return bookingCache.get("booking:" + bookingId, k -> {
            log.debug("Cache miss for getRoomsByBookingId: {}", bookingId);
            return repository.findByBookingId(bookingId);
        });
    }

    @Override
    public int updateStatusByLockId(String lockId, RoomStatus status) {
        return 0;
    }

    @Override
    public int releaseExpiredLocks(LocalDateTime now) {
        return 0;
    }

    /**
     * Save with cache invalidation - very important to maintain cache consistency
     */
    @Override
    @CacheEvict(value = {
            CacheConfig.CacheNames.ROOM_AVAILABILITY_BY_LOCK,
            CacheConfig.CacheNames.ROOM_AVAILABILITY_BY_BOOKING
    }, allEntries = true)
    public List<RoomAvailability> saveAll(List<RoomAvailability> roomAvailabilities) {
        // First, invalidate specific cache entries that we know will change
        invalidateCacheEntries(roomAvailabilities);

        // Then save to database
        return repository.saveAll(roomAvailabilities);
    }

    @Override
    public List<RoomAvailability> getAvailabilitiesByRoomIdAndDateRange(Long roomId, LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public RoomAvailability getAvailabilityByRoomIdAndDate(Long roomId, LocalDate date) {
        return null;
    }

    @Override
    public int releaseLocksById(String lockId) {
        return 0;
    }

    /**
     * Intelligently invalidate cache entries based on the updated entities
     */
    private void invalidateCacheEntries(List<RoomAvailability> roomAvailabilities) {
        // Collect unique lockIds and bookingIds
        List<String> lockIds = roomAvailabilities.stream()
                .map(RoomAvailability::getLockId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Long> bookingIds = roomAvailabilities.stream()
                .map(RoomAvailability::getBookingId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // Invalidate specific cache entries
        lockIds.forEach(lockId -> lockCache.invalidate("lock:" + lockId));
        bookingIds.forEach(bookingId -> bookingCache.invalidate("booking:" + bookingId));

        log.debug("Invalidated {} lock caches and {} booking caches",
                lockIds.size(), bookingIds.size());
    }
}
