package huy.project.inventory_service.infrastructure.repository;

import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    List<RoomAvailability> findByLockId(String lockId);
    
    List<RoomAvailability> findByBookingId(Long bookingId);
    
    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.roomId = :roomId AND ra.date BETWEEN :startDate AND :endDate")
    List<RoomAvailability> findByRoomIdAndDateRange(Long roomId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT ra FROM RoomAvailability ra WHERE ra.accommodationId = :accommodationId AND ra.date BETWEEN :startDate AND :endDate")
    List<RoomAvailability> findByAccommodationIdAndDateRange(Long accommodationId, LocalDate startDate, LocalDate endDate);
}
