package huy.project.inventory_service.kernel.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Cache key generator utility
 * Similar to how Netflix and Booking.com standardize cache keys
 */
public final class CacheKeyGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String LOCK_KEY_FORMAT = "lock:%s";
    private static final String BOOKING_KEY_FORMAT = "booking:%d";
    private static final String ROOM_KEY_FORMAT = "room:%d";
    private static final String ACCOMMODATION_KEY_FORMAT = "accommodation:%d";
    private static final String DATE_RANGE_KEY_FORMAT = "date-range:%s:%s";
    private static final String IDEMPOTENT_REQUEST_KEY_FORMAT = "idempotent:%s:%s:%s";
    
    private CacheKeyGenerator() {
        // Utility class
    }
    
    /**
     * Generate a cache key for a lock
     */
    public static String forLock(String lockId) {
        return String.format(LOCK_KEY_FORMAT, lockId);
    }
    
    /**
     * Generate a cache key for a booking
     */
    public static String forBooking(Long bookingId) {
        return String.format(BOOKING_KEY_FORMAT, bookingId);
    }
    
    /**
     * Generate a cache key for a room on a specific date
     */
    public static String forRoomOnDate(Long roomId, LocalDate date) {
        return String.format("%s:%s", forRoom(roomId), formatDate(date));
    }
    
    /**
     * Generate a cache key for a room
     */
    public static String forRoom(Long roomId) {
        return String.format(ROOM_KEY_FORMAT, roomId);
    }
    
    /**
     * Generate a cache key for an accommodation
     */
    public static String forAccommodation(Long accommodationId) {
        return String.format(ACCOMMODATION_KEY_FORMAT, accommodationId);
    }
    
    /**
     * Generate a cache key for a date range
     */
    public static String forDateRange(LocalDate startDate, LocalDate endDate) {
        return String.format(DATE_RANGE_KEY_FORMAT, formatDate(startDate), formatDate(endDate));
    }
    
    /**
     * Generate a cache key for an idempotent request
     */
    public static String forIdempotentRequest(String requestType, String entityId, String identifier) {
        return String.format(IDEMPOTENT_REQUEST_KEY_FORMAT, requestType, entityId, identifier);
    }
    
    /**
     * Format date for cache keys
     */
    private static String formatDate(LocalDate date) {
        return date.format(DATE_FORMAT);
    }
}
