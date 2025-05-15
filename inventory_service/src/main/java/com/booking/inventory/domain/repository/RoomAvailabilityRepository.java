package com.booking.inventory.domain.repository;

import com.booking.inventory.domain.model.RoomAvailability;
import com.booking.inventory.domain.model.RoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {
    
    // Basic queries
    List<RoomAvailability> findByRoomId(Long roomId);
    
    List<RoomAvailability> findByRoomIdAndDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);
    
    // Changed to Optional since we should have only one availability per room and date
    Optional<RoomAvailability> findByRoomIdAndDate(Long roomId, LocalDate date);
    
    // Find all availabilities by booking ID
    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.bookingId = :bookingId")
    List<RoomAvailability> findByBookingId(@Param("bookingId") String bookingId);
    
    // Property level queries
    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.room.property.id = :propertyId AND ra.date BETWEEN :startDate AND :endDate")
    List<RoomAvailability> findByPropertyIdAndDateBetween(
            @Param("propertyId") Long propertyId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.room.property.id = :propertyId AND ra.date BETWEEN :startDate AND :endDate AND ra.status = :status")
    List<RoomAvailability> findByPropertyIdAndDateBetweenAndStatus(
            @Param("propertyId") Long propertyId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate, 
            @Param("status") RoomStatus status);
    
    // Room type level queries
    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.room.roomType.id = :roomTypeId AND ra.date BETWEEN :startDate AND :endDate AND ra.status = :status")
    List<RoomAvailability> findByRoomTypeIdAndDateBetweenAndStatus(
            @Param("roomTypeId") Long roomTypeId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate, 
            @Param("status") RoomStatus status);
    
    // Maintenance and cleaning related queries
    List<RoomAvailability> findByStatusAndLockExpirationDateBefore(RoomStatus status, LocalDate expiryDate);
    
    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.needsCleaning = true")
    List<RoomAvailability> findRoomsNeedingCleaning();
    
    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.needsMaintenance = true")
    List<RoomAvailability> findRoomsNeedingMaintenance();
    
    // Batch update methods
    @Modifying
    @Query("UPDATE RoomAvailability ra SET ra.needsCleaning = false, ra.status = 'AVAILABLE', ra.lastModified = CURRENT_TIMESTAMP WHERE ra.status = 'CLEANING' AND ra.room.id = :roomId")
    int markRoomAsClean(@Param("roomId") Long roomId);
    
    @Modifying
    @Query("UPDATE RoomAvailability ra SET ra.lastModified = :lastModified WHERE ra.bookingId = :bookingId")
    int updateLastModifiedByBookingId(@Param("bookingId") String bookingId, @Param("lastModified") LocalDateTime lastModified);
    
    // Occupancy and revenue analysis
    @Query("SELECT COUNT(ra) FROM RoomAvailability ra WHERE ra.room.property.id = :propertyId AND ra.date = :date AND ra.status IN ('BOOKED', 'OCCUPIED')")
    long countBookedOrOccupiedRoomsByPropertyAndDate(@Param("propertyId") Long propertyId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(ra) FROM RoomAvailability ra WHERE ra.room.property.id = :propertyId AND ra.date = :date")
    long countTotalRoomsByPropertyAndDate(@Param("propertyId") Long propertyId, @Param("date") LocalDate date);
    
    // Paginated query for large datasets
    Page<RoomAvailability> findByRoomIdAndDateGreaterThanEqual(Long roomId, LocalDate date, Pageable pageable);
}
