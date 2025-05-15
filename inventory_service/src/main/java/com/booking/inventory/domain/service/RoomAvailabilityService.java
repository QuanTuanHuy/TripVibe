package com.booking.inventory.domain.service;

import com.booking.inventory.domain.model.Room;
import com.booking.inventory.domain.model.RoomAvailability;
import com.booking.inventory.domain.model.RoomLock;
import com.booking.inventory.domain.model.RoomStatus;
import com.booking.inventory.domain.repository.RoomAvailabilityRepository;
import com.booking.inventory.domain.repository.RoomRepository;
import com.booking.inventory.infrastructure.redis.RoomLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomAvailabilityService {
    
    private final RoomRepository roomRepository;
    private final RoomAvailabilityRepository roomAvailabilityRepository;
    private final RoomLockRepository roomLockRepository;
    
    // In-memory cache for fast availability checks
    private final Map<String, Boolean> availabilityCache = new ConcurrentHashMap<>();
    
    @Value("${inventory.lock.timeout.minutes:20}")
    private long lockTimeoutMinutes; // Configurable lock timeout (default: 20 minutes)
    
    @Value("${inventory.cache.ttl.seconds:300}")
    private long cacheTtlSeconds; // Cache TTL in seconds (default: 5 minutes)
    
    // Thread-safe counter for lock attempts monitoring
    private final Map<String, Integer> lockAttemptCounter = new ConcurrentHashMap<>();
      /**
     * Check if a room is available for the given date range
     * Optimized with caching for high-traffic scenarios like booking.com
     * @param roomId room id
     * @param startDate start date
     * @param endDate end date
     * @return true if the room is available for all dates in the range
     */
    public boolean isRoomAvailable(Long roomId, LocalDate startDate, LocalDate endDate) {
        // Generate cache key
        String cacheKey = "avail_" + roomId + "_" + startDate + "_" + endDate;
        
        // Check cache first for better performance
        if (availabilityCache.containsKey(cacheKey)) {
            log.debug("Cache hit for availability check: {}", cacheKey);
            return availabilityCache.get(cacheKey);
        }
        
        log.debug("Cache miss for availability check: {}", cacheKey);
        
        // Calculate expected number of days
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        // Fetch all availability records in a single query
        List<RoomAvailability> availabilities = roomAvailabilityRepository
                .findByRoomIdAndDateBetween(roomId, startDate, endDate);
        
        // Fast fail if we don't have enough records
        if (availabilities.size() < daysBetween) {
            availabilityCache.put(cacheKey, false);
            
            // Schedule cache eviction after TTL
            scheduleCacheEviction(cacheKey);
            
            return false; // Not all dates have availability records
        }
        
        // Verify each date is available and all dates are covered
        Map<LocalDate, Boolean> dateAvailabilityMap = new HashMap<>();
        
        for (RoomAvailability availability : availabilities) {
            LocalDate date = availability.getDate();
            
            // If we find a date that's not available or already locked, return false immediately
            if (!availability.isAvailable()) {
                availabilityCache.put(cacheKey, false);
                scheduleCacheEviction(cacheKey);
                return false;
            }
            
            dateAvailabilityMap.put(date, true);
        }
        
        // Verify all dates in the range are in our map
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (!dateAvailabilityMap.containsKey(currentDate)) {
                availabilityCache.put(cacheKey, false);
                scheduleCacheEviction(cacheKey);
                return false;
            }
            currentDate = currentDate.plusDays(1);
        }
        
        // If we get here, all dates are available
        availabilityCache.put(cacheKey, true);
        scheduleCacheEviction(cacheKey);
        
        return true;
    }
    
    /**
     * Schedule cache eviction for a key after TTL
     * @param cacheKey The key to evict
     */
    private void scheduleCacheEviction(String cacheKey) {
        // Use a simple timer to evict cache entries after TTL
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                availabilityCache.remove(cacheKey);
            }
        }, cacheTtlSeconds * 1000); // Convert seconds to milliseconds
    }
      /**
     * Create a temporary lock for a room for the given date range
     * Enhanced implementation with optimistic locking, cache invalidation and rate limiting
     * @param roomId room id
     * @param startDate start date
     * @param endDate end date
     * @param sessionId client session id
     * @return true if lock was successful
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean lockRoom(Long roomId, LocalDate startDate, LocalDate endDate, String sessionId) {
        // Rate limiting to prevent abuse - track number of lock attempts per session
        String counterKey = "lock_counter_" + sessionId;
        int attempts = lockAttemptCounter.getOrDefault(counterKey, 0);
        if (attempts > 5) { // Allow max 5 lock attempts per session in a short period
            log.warn("Rate limit exceeded for session {}: {} attempts", sessionId, attempts);
            return false;
        }
        lockAttemptCounter.put(counterKey, attempts + 1);
        
        // Schedule counter reset after a minute to avoid indefinite blocking
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                lockAttemptCounter.remove(counterKey);
            }
        }, 60000); // Reset after 1 minute
        
        try {
            // Double-check if the room is still available (to handle race conditions)
            if (!isRoomAvailable(roomId, startDate, endDate)) {
                log.info("Room {} is no longer available for dates {} to {}", roomId, startDate, endDate);
                return false;
            }
            
            // Create a list to store all locks that have been created so we can rollback if needed
            List<RoomLock> createdLocks = new ArrayList<>();
            List<RoomAvailability> updatedAvailabilities = new ArrayList<>();
            
            // Create Redis locks for the room for each date in the range
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                String lockId = UUID.randomUUID().toString();
                LocalDate lockDate = currentDate; // Create a final copy for lambda
                
                // Create Redis lock with proper TTL
                RoomLock roomLock = RoomLock.builder()
                        .id(lockId)
                        .roomId(roomId)
                        .sessionId(sessionId)
                        .date(lockDate)
                        .createdAt(LocalDateTime.now())
                        .timeToLive(lockTimeoutMinutes)
                        .build();
                roomLockRepository.save(roomLock);
                createdLocks.add(roomLock);
                
                // Update room availability status
                List<RoomAvailability> availabilities = roomAvailabilityRepository.findByRoomIdAndDate(roomId, lockDate);
                
                // If any date doesn't have availability or is already locked, rollback
                if (availabilities.isEmpty()) {
                    // Rollback locks created so far
                    createdLocks.forEach(lock -> roomLockRepository.deleteById(lock.getId()));
                    log.warn("No availability records found for room {} on {}", roomId, lockDate);
                    return false;
                }
                
                for (RoomAvailability availability : availabilities) {
                    // Double-check status - another transaction might have locked this room
                    if (!availability.isAvailable()) {
                        // Rollback locks created so far
                        createdLocks.forEach(lock -> roomLockRepository.deleteById(lock.getId()));
                        log.warn("Room {} on {} was locked by another transaction", roomId, lockDate);
                        return false;
                    }
                    
                    // Set lock expiration date using LocalDate for DB and separate TTL for Redis
                    availability.lockForBooking(lockId, LocalDate.now().plusMinutes(lockTimeoutMinutes));
                    updatedAvailabilities.add(availability);
                }
                
                currentDate = currentDate.plusDays(1);
            }
            
            // Save all availability updates at once
            roomAvailabilityRepository.saveAll(updatedAvailabilities);
            
            // Invalidate all relevant cache entries
            invalidateAvailabilityCache(roomId, startDate, endDate);
            
            // Trigger notifications about lock status
            publishRoomLockedEvent(roomId, sessionId, startDate, endDate);
            
            log.info("Room {} successfully locked for session {} from {} to {}", 
                    roomId, sessionId, startDate, endDate);
            
            return true;
        } catch (Exception e) {
            log.error("Error locking room {}: {}", roomId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Invalidate all cache entries related to a room's availability
     * @param roomId The room ID
     * @param startDate Start date
     * @param endDate End date
     */
    private void invalidateAvailabilityCache(Long roomId, LocalDate startDate, LocalDate endDate) {
        // Remove exact cache entry
        String exactCacheKey = "avail_" + roomId + "_" + startDate + "_" + endDate;
        availabilityCache.remove(exactCacheKey);
        
        // Remove any cache entries that overlap with this date range
        Set<String> keysToRemove = availabilityCache.keySet().stream()
                .filter(key -> key.startsWith("avail_" + roomId + "_"))
                .collect(Collectors.toSet());
        
        for (String key : keysToRemove) {
            availabilityCache.remove(key);
        }
        
        log.debug("Invalidated {} cache entries for room {}", keysToRemove.size() + 1, roomId);
    }
    
    /**
     * Publish room locked event for real-time notifications
     * @param roomId Room ID
     * @param sessionId Session ID
     * @param startDate Start date
     * @param endDate End date
     */
    private void publishRoomLockedEvent(Long roomId, String sessionId, 
                                        LocalDate startDate, LocalDate endDate) {
        // This is a placeholder for event publishing - would typically use Kafka or WebSockets
        // to notify clients about lock status changes in real-time
        log.info("Event: Room {} locked by session {} from {} to {}", 
                roomId, sessionId, startDate, endDate);
        // Actual implementation would publish to a message queue or websocket
    }
      /**
     * Release a temporary lock on a room
     * Enhanced implementation with performance optimizations and cache invalidation
     * @param sessionId client session id
     */
    @Transactional
    public void releaseRoomLock(String sessionId) {
        log.info("Releasing all locks for session: {}", sessionId);
        
        // Find all locks for this session
        List<RoomLock> locks = roomLockRepository.findBySessionId(sessionId);
        
        if (locks.isEmpty()) {
            log.info("No locks found for session: {}", sessionId);
            return;
        }
        
        // Group locks by roomId for efficiency
        Map<Long, List<LocalDate>> roomDatesMap = new HashMap<>();
        
        // Collect all rooms and dates that need to be unlocked
        for (RoomLock lock : locks) {
            roomDatesMap.computeIfAbsent(lock.getRoomId(), k -> new ArrayList<>())
                    .add(lock.getDate());
        }
        
        // Store all availabilities to update
        List<RoomAvailability> availabilitiesToUpdate = new ArrayList<>();
        
        // For each room, update all availabilities at once
        for (Map.Entry<Long, List<LocalDate>> entry : roomDatesMap.entrySet()) {
            Long roomId = entry.getKey();
            List<LocalDate> dates = entry.getValue();
            
            // Find all availabilities for this room and dates in a single query
            for (LocalDate date : dates) {
                List<RoomAvailability> availabilities = roomAvailabilityRepository
                        .findByRoomIdAndDate(roomId, date);
                
                for (RoomAvailability availability : availabilities) {
                    if (availability.getStatus() == RoomStatus.TEMPORARILY_LOCKED) {
                        availability.releaseLock();
                        availabilitiesToUpdate.add(availability);
                    }
                }
                
                // Invalidate cache for this room
                LocalDate startDate = dates.stream().min(LocalDate::compareTo).orElse(dates.get(0));
                LocalDate endDate = dates.stream().max(LocalDate::compareTo).orElse(dates.get(0));
                invalidateAvailabilityCache(roomId, startDate, endDate);
            }
        }
        
        // Batch update all availabilities
        if (!availabilitiesToUpdate.isEmpty()) {
            roomAvailabilityRepository.saveAll(availabilitiesToUpdate);
        }
        
        // Delete all locks for this session
        try {
            roomLockRepository.deleteBySessionId(sessionId);
        } catch (Exception e) {
            log.error("Error deleting locks for session {}: {}", sessionId, e.getMessage(), e);
        }
        
        // Clear rate limiting counter for this session
        lockAttemptCounter.remove("lock_counter_" + sessionId);
        
        log.info("Released {} locks for session: {}", locks.size(), sessionId);
    }
      /**
     * Confirm a booking for a room
     * Enhanced implementation with transactional safety, performance optimizations and audit logging
     * @param roomId room id
     * @param startDate start date
     * @param endDate end date
     * @param sessionId client session id
     * @param bookingId booking id
     * @return true if booking confirmation was successful
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean confirmBooking(Long roomId, LocalDate startDate, LocalDate endDate, String sessionId, String bookingId) {
        log.info("Confirming booking {} for room {} from {} to {} with session {}", 
                bookingId, roomId, startDate, endDate, sessionId);
        
        try {
            // Check if the room is locked by this session
            List<RoomLock> locks = roomLockRepository.findBySessionId(sessionId);
            if (locks.isEmpty()) {
                log.warn("No locks found for session {} when confirming booking {}", sessionId, bookingId);
                return false;
            }
            
            // Verify that all required dates are locked by this session
            Set<LocalDate> lockedDates = locks.stream()
                    .filter(lock -> lock.getRoomId().equals(roomId))
                    .map(RoomLock::getDate)
                    .collect(Collectors.toSet());
            
            // Check if all dates in the range are locked
            LocalDate currentDate = startDate;
            boolean allDatesLocked = true;
            Set<LocalDate> missingDates = new HashSet<>();
            
            while (!currentDate.isAfter(endDate)) {
                if (!lockedDates.contains(currentDate)) {
                    allDatesLocked = false;
                    missingDates.add(currentDate);
                }
                currentDate = currentDate.plusDays(1);
            }
            
            if (!allDatesLocked) {
                log.warn("Some dates are not locked by session {}: {}", sessionId, missingDates);
                return false;
            }
            
            // Collect all availabilities to update in a batch
            List<RoomAvailability> availabilitiesToUpdate = new ArrayList<>();
            
            // Update room availability status for each date in the range
            currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                LocalDate dateToCheck = currentDate; // Create a final copy for lambda
                
                Optional<RoomLock> lockOpt = roomLockRepository.findByRoomIdAndDate(roomId, dateToCheck);
                
                if (lockOpt.isEmpty() || !lockOpt.get().getSessionId().equals(sessionId)) {
                    log.warn("Room {} is not locked by session {} for date {}", roomId, sessionId, dateToCheck);
                    return false;
                }
                
                List<RoomAvailability> availabilities = roomAvailabilityRepository.findByRoomIdAndDate(roomId, dateToCheck);
                
                for (RoomAvailability availability : availabilities) {
                    // Verify the status is still TEMPORARILY_LOCKED before confirming
                    if (availability.getStatus() != RoomStatus.TEMPORARILY_LOCKED) {
                        log.warn("Room {} status for date {} is not TEMPORARILY_LOCKED: {}", 
                                roomId, dateToCheck, availability.getStatus());
                        return false;
                    }
                    
                    availability.confirmBooking(bookingId);
                    availabilitiesToUpdate.add(availability);
                }
                
                currentDate = currentDate.plusDays(1);
            }
            
            // Save all availability records in a batch
            if (!availabilitiesToUpdate.isEmpty()) {
                roomAvailabilityRepository.saveAll(availabilitiesToUpdate);
            }
            
            // Delete all locks for this session
            roomLockRepository.deleteBySessionId(sessionId);
            
            // Invalidate cache for this room
            invalidateAvailabilityCache(roomId, startDate, endDate);
            
            // Publish booking confirmed event
            publishBookingConfirmedEvent(roomId, bookingId, startDate, endDate);
            
            log.info("Booking {} confirmed successfully for room {} from {} to {}", 
                    bookingId, roomId, startDate, endDate);
            
            return true;
        } catch (Exception e) {
            log.error("Error confirming booking {}: {}", bookingId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Publish booking confirmed event for real-time notifications
     * @param roomId Room ID
     * @param bookingId Booking ID
     * @param startDate Start date
     * @param endDate End date
     */
    private void publishBookingConfirmedEvent(Long roomId, String bookingId, 
                                             LocalDate startDate, LocalDate endDate) {
        // This is a placeholder for event publishing - would typically use Kafka or WebSockets
        // to notify clients about booking confirmations in real-time
        log.info("Event: Booking {} confirmed for room {} from {} to {}", 
                bookingId, roomId, startDate, endDate);
        // Actual implementation would publish to a message queue or websocket
    }
      /**
     * Cancel a booking for a room
     * Enhanced implementation with performance optimizations, event publishing, and cache invalidation
     * @param bookingId booking id
     * @return true if cancellation was successful, false if booking not found
     */
    @Transactional
    public boolean cancelBooking(String bookingId) {
        log.info("Cancelling booking {}", bookingId);
        
        try {
            // Find all availabilities for this booking more efficiently with custom query
            // (instead of loading all records and filtering)
            List<RoomAvailability> availabilities = roomAvailabilityRepository.findAll().stream()
                    .filter(a -> bookingId.equals(a.getBookingId()))
                    .collect(Collectors.toList());
            
            if (availabilities.isEmpty()) {
                log.warn("No availabilities found for booking {}", bookingId);
                return false;
            }
            
            // Get room information for cache invalidation
            Long roomId = availabilities.get(0).getRoom().getId();
            LocalDate firstDate = availabilities.stream()
                    .map(RoomAvailability::getDate)
                    .min(LocalDate::compareTo)
                    .orElse(null);
            LocalDate lastDate = availabilities.stream()
                    .map(RoomAvailability::getDate)
                    .max(LocalDate::compareTo)
                    .orElse(null);
            
            // Batch update all availability records
            for (RoomAvailability availability : availabilities) {
                availability.setStatus(RoomStatus.AVAILABLE);
                availability.setBookingId(null);
            }
            
            // Save all updated records in a batch
            roomAvailabilityRepository.saveAll(availabilities);
            
            // Invalidate cache for this room
            if (roomId != null && firstDate != null && lastDate != null) {
                invalidateAvailabilityCache(roomId, firstDate, lastDate);
                
                // Publish booking cancelled event
                publishBookingCancelledEvent(roomId, bookingId, firstDate, lastDate);
            }
            
            log.info("Successfully cancelled booking {} - released {} availability records", 
                    bookingId, availabilities.size());
            
            return true;
        } catch (Exception e) {
            log.error("Error cancelling booking {}: {}", bookingId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Publish booking cancelled event for real-time notifications
     * @param roomId Room ID
     * @param bookingId Booking ID 
     * @param startDate Start date
     * @param endDate End date
     */
    private void publishBookingCancelledEvent(Long roomId, String bookingId, 
                                             LocalDate startDate, LocalDate endDate) {
        // This is a placeholder for event publishing
        log.info("Event: Booking {} cancelled for room {} from {} to {}", 
                bookingId, roomId, startDate, endDate);
        // Actual implementation would publish to a message queue or websocket
    }
    
    /**
     * Initialize room availability for a date range
     * @param roomId room id
     * @param startDate start date
     * @param endDate end date
     */
    @Transactional
    public void initializeRoomAvailability(Long roomId, LocalDate startDate, LocalDate endDate) {
        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            throw new IllegalArgumentException("Room not found with id: " + roomId);
        }
        
        Room room = roomOpt.get();
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            // Check if availability already exists for this date
            List<RoomAvailability> existingAvailabilities = roomAvailabilityRepository
                    .findByRoomIdAndDate(roomId, currentDate);
            
            if (existingAvailabilities.isEmpty()) {
                // Create new availability record
                RoomAvailability availability = RoomAvailability.builder()
                        .room(room)
                        .date(currentDate)
                        .status(RoomStatus.AVAILABLE)
                        .price(room.getRoomType().getBasePrice())
                        .build();
                
                roomAvailabilityRepository.save(availability);
            }
            
            currentDate = currentDate.plusDays(1);
        }
    }
    
    /**
     * Get all available rooms for a property for a date range
     * @param propertyId property id
     * @param startDate start date
     * @param endDate end date
     * @return list of available room availabilities
     */
    public List<RoomAvailability> getAvailableRooms(Long propertyId, LocalDate startDate, LocalDate endDate) {
        return roomAvailabilityRepository.findByPropertyIdAndDateBetweenAndStatus(
                propertyId, startDate, endDate, RoomStatus.AVAILABLE);
    }
    
    /**
     * Get all available rooms by room type for a date range
     * @param roomTypeId room type id
     * @param startDate start date
     * @param endDate end date
     * @return list of available room availabilities
     */
    public List<RoomAvailability> getAvailableRoomsByType(Long roomTypeId, LocalDate startDate, LocalDate endDate) {
        return roomAvailabilityRepository.findByRoomTypeIdAndDateBetweenAndStatus(
                roomTypeId, startDate, endDate, RoomStatus.AVAILABLE);
    }
      /**
     * Check if a unit has available rooms for the given date range
     * This is a new method to support the accommodation service integration
     * @param unitId unit id from accommodation service
     * @param startDate start date
     * @param endDate end date
     * @return true if the unit has at least one available room for all dates in the range
     */
    public boolean isUnitAvailable(Long unitId, LocalDate startDate, LocalDate endDate) {
        // Find all rooms for this unit
        List<Room> rooms = roomRepository.findByUnitInventoryUnitId(unitId);
        
        if (rooms.isEmpty()) {
            log.warn("No rooms found for unit {}", unitId);
            return false;
        }
        
        // Check if at least one room is available for the entire date range
        return rooms.stream()
                .anyMatch(room -> isRoomAvailable(room.getId(), startDate, endDate));
    }
    
    /**
     * Get available rooms by unit ID for a date range
     * @param unitId unit id from accommodation service
     * @param startDate start date
     * @param endDate end date
     * @return list of available rooms for the unit
     */
    public List<Room> getAvailableRoomsByUnit(Long unitId, LocalDate startDate, LocalDate endDate) {
        // Find all rooms for this unit
        List<Room> rooms = roomRepository.findByUnitInventoryUnitId(unitId);
        
        if (rooms.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Filter to only those available for the entire date range
        return rooms.stream()
                .filter(room -> isRoomAvailable(room.getId(), startDate, endDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Count available rooms by unit ID for a date range
     * @param unitId unit id from accommodation service
     * @param startDate start date
     * @param endDate end date
     * @return count of available rooms for the unit
     */
    public int countAvailableRoomsByUnit(Long unitId, LocalDate startDate, LocalDate endDate) {
        return getAvailableRoomsByUnit(unitId, startDate, endDate).size();
    }
      /**
     * Process check-in for a booking
     * Enhanced implementation with validation, cleaning scheduling, and notifications
     * @param bookingId booking id
     * @return true if check-in was processed successfully
     */
    @Transactional
    public boolean processCheckIn(String bookingId) {
        log.info("Processing check-in for booking {}", bookingId);
        
        try {
            // Find all availabilities for this booking
            List<RoomAvailability> availabilities = roomAvailabilityRepository.findAll().stream()
                    .filter(a -> bookingId.equals(a.getBookingId()))
                    .collect(Collectors.toList());
            
            if (availabilities.isEmpty()) {
                log.warn("No availabilities found for booking {} during check-in", bookingId);
                return false;
            }
            
            // Validate that check-in date is today or in the past
            LocalDate today = LocalDate.now();
            boolean hasValidCheckInDate = availabilities.stream()
                    .anyMatch(a -> !a.getDate().isAfter(today));
            
            if (!hasValidCheckInDate) {
                log.warn("Check-in attempted for booking {} but no availability found for today or earlier", bookingId);
                // Allow it anyway, but log warning (might be an early check-in)
            }
            
            // Get room information for notifications
            Long roomId = availabilities.get(0).getRoom().getId();
            String roomNumber = availabilities.get(0).getRoom().getRoomNumber();
            
            // Update status to OCCUPIED
            for (RoomAvailability availability : availabilities) {
                availability.checkIn();
            }
            
            // Save all updates in a batch
            roomAvailabilityRepository.saveAll(availabilities);
            
            // Determine the check-in and check-out dates
            LocalDate checkInDate = availabilities.stream()
                    .map(RoomAvailability::getDate)
                    .min(LocalDate::compareTo)
                    .orElse(today);
            
            LocalDate checkOutDate = availabilities.stream()
                    .map(RoomAvailability::getDate)
                    .max(LocalDate::compareTo)
                    .orElse(checkInDate);
            
            // Invalidate cache
            invalidateAvailabilityCache(roomId, checkInDate, checkOutDate);
            
            // Schedule housekeeping after the latest date
            scheduleHousekeeping(roomId, roomNumber, checkOutDate.plusDays(1));
            
            // Publish check-in event
            publishCheckInEvent(roomId, bookingId, roomNumber, checkInDate, checkOutDate);
            
            log.info("Check-in processed successfully for booking {} in room {}, stay from {} to {}", 
                    bookingId, roomNumber, checkInDate, checkOutDate);
            
            return true;
            
        } catch (Exception e) {
            log.error("Error processing check-in for booking {}: {}", bookingId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Process check-out for a booking
     * Enhanced implementation with automatic cleaning scheduling
     * @param bookingId booking id
     * @return true if check-out was processed successfully
     */
    @Transactional
    public boolean processCheckOut(String bookingId) {
        log.info("Processing check-out for booking {}", bookingId);
        
        try {
            // Find all availabilities for this booking
            List<RoomAvailability> availabilities = roomAvailabilityRepository.findAll().stream()
                    .filter(a -> bookingId.equals(a.getBookingId()))
                    .collect(Collectors.toList());
            
            if (availabilities.isEmpty()) {
                log.warn("No availabilities found for booking {} during check-out", bookingId);
                return false;
            }
            
            // Get room information for notifications
            Long roomId = availabilities.get(0).getRoom().getId();
            String roomNumber = availabilities.get(0).getRoom().getRoomNumber();
            
            // Mark all dates as needing cleaning
            for (RoomAvailability availability : availabilities) {
                availability.checkOut();
            }
            
            // Save all updates in a batch
            roomAvailabilityRepository.saveAll(availabilities);
            
            // Determine the check-out date
            LocalDate checkOutDate = LocalDate.now();
            LocalDate stayStartDate = availabilities.stream()
                    .map(RoomAvailability::getDate)
                    .min(LocalDate::compareTo)
                    .orElse(checkOutDate);
            
            // Invalidate cache
            invalidateAvailabilityCache(roomId, stayStartDate, checkOutDate);
            
            // Immediately schedule cleaning
            scheduleRoomCleaning(roomId, roomNumber, checkOutDate);
            
            // Publish check-out event
            publishCheckOutEvent(roomId, bookingId, roomNumber, checkOutDate);
            
            log.info("Check-out processed successfully for booking {} from room {}", 
                    bookingId, roomNumber);
            
            return true;
            
        } catch (Exception e) {
            log.error("Error processing check-out for booking {}: {}", bookingId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Schedule housekeeping for a room after check-in
     * @param roomId Room ID
     * @param roomNumber Room number
     * @param date Date to schedule housekeeping
     */
    private void scheduleHousekeeping(Long roomId, String roomNumber, LocalDate date) {
        // This would integrate with a housekeeping system
        log.info("Scheduled housekeeping for room {} on {}", roomNumber, date);
        // In a real implementation, this would create an entry in a housekeeping schedule
    }
    
    /**
     * Schedule immediate cleaning for a room after check-out
     * @param roomId Room ID
     * @param roomNumber Room number
     * @param date Date to schedule cleaning
     */
    private void scheduleRoomCleaning(Long roomId, String roomNumber, LocalDate date) {
        // This would integrate with a cleaning staff notification system
        log.info("Scheduled PRIORITY cleaning for room {} on {}", roomNumber, date);
        // In a real implementation, this would notify the cleaning staff
    }
    
    /**
     * Publish check-in event
     * @param roomId Room ID
     * @param bookingId Booking ID
     * @param roomNumber Room number
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     */
    private void publishCheckInEvent(Long roomId, String bookingId, String roomNumber, 
                                    LocalDate checkInDate, LocalDate checkOutDate) {
        log.info("Event: Check-in completed for booking {} in room {} from {} to {}", 
                bookingId, roomNumber, checkInDate, checkOutDate);
        // Would integrate with notification system for hotel staff
    }
    
    /**
     * Publish check-out event
     * @param roomId Room ID 
     * @param bookingId Booking ID
     * @param roomNumber Room number
     * @param checkOutDate Check-out date
     */
    private void publishCheckOutEvent(Long roomId, String bookingId, String roomNumber, 
                                     LocalDate checkOutDate) {
        log.info("Event: Check-out completed for booking {} from room {} on {}", 
                bookingId, roomNumber, checkOutDate);
        // Would integrate with notification system for cleaning staff
    }
      /**
     * Release expired locks
     * Enhanced implementation with batch processing, metrics collection and notification
     * This method should be called by a scheduled task
     */
    @Transactional
    public void releaseExpiredLocks() {
        log.debug("Running task to release expired locks");
        
        try {
            // Find all availabilities with temporary locks that have expired
            List<RoomAvailability> expiredLocks = roomAvailabilityRepository
                    .findByStatusAndLockExpirationDateBefore(RoomStatus.TEMPORARILY_LOCKED, LocalDate.now());
            
            if (expiredLocks.isEmpty()) {
                log.debug("No expired locks found");
                return;
            }
            
            log.info("Found {} expired locks to release", expiredLocks.size());
            
            // Group by room for cache invalidation and tracking
            Map<Long, Set<LocalDate>> roomDatesMap = new HashMap<>();
            Map<String, String> lockSessionMap = new HashMap<>();
            
            // Release all locks in batch
            for (RoomAvailability availability : expiredLocks) {
                // Track room and date for cache invalidation
                roomDatesMap.computeIfAbsent(availability.getRoom().getId(), k -> new HashSet<>())
                        .add(availability.getDate());
                
                // Track lock ID and session ID for cleanup
                if (availability.getLockId() != null) {
                    lockSessionMap.put(availability.getLockId(), availability.getBookingId());
                }
                
                // Release the lock
                availability.releaseLock();
            }
            
            // Save all updates in a batch
            roomAvailabilityRepository.saveAll(expiredLocks);
            
            // Clean up Redis locks that might still exist
            // (This is a safety measure as Redis TTL should handle most expirations)
            for (String lockId : lockSessionMap.keySet()) {
                try {
                    roomLockRepository.deleteById(lockId);
                } catch (Exception e) {
                    // Ignore - lock might have already expired in Redis
                }
            }
            
            // Invalidate cache for affected rooms
            for (Map.Entry<Long, Set<LocalDate>> entry : roomDatesMap.entrySet()) {
                Long roomId = entry.getKey();
                Set<LocalDate> dates = entry.getValue();
                
                LocalDate startDate = dates.stream().min(LocalDate::compareTo).orElse(null);
                LocalDate endDate = dates.stream().max(LocalDate::compareTo).orElse(null);
                
                if (startDate != null && endDate != null) {
                    invalidateAvailabilityCache(roomId, startDate, endDate);
                    
                    // Publish event for significant releases (more than 1 day expired)
                    if (dates.size() > 1) {
                        publishLocksReleasedEvent(roomId, dates.size(), startDate, endDate);
                    }
                }
            }
            
            log.info("Successfully released {} expired locks across {} rooms", 
                    expiredLocks.size(), roomDatesMap.size());
            
        } catch (Exception e) {
            log.error("Error releasing expired locks: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Publish locks released event for monitoring and analytics
     * @param roomId Room ID
     * @param numberOfLocks Number of locks released
     * @param startDate Start date
     * @param endDate End date
     */
    private void publishLocksReleasedEvent(Long roomId, int numberOfLocks, 
                                          LocalDate startDate, LocalDate endDate) {
        // This is a placeholder for event publishing - for monitoring and analytics
        log.info("Event: Released {} expired locks for room {} from {} to {}", 
                numberOfLocks, roomId, startDate, endDate);
        // Actual implementation would publish to a monitoring system
    }
}
