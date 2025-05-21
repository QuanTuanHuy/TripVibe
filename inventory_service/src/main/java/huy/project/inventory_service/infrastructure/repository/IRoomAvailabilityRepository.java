package huy.project.inventory_service.infrastructure.repository;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.infrastructure.repository.model.RoomAvailabilityModel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IRoomAvailabilityRepository extends IBaseRepository<RoomAvailabilityModel> {
    List<RoomAvailabilityModel> findByRoomIdAndDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);

    Optional<RoomAvailabilityModel> findByRoomIdAndDate(Long roomId, LocalDate date);

    List<RoomAvailabilityModel> findByLockId(String lockId);

    List<RoomAvailabilityModel> findByBookingId(Long bookingId);

    @Modifying
    @Query("UPDATE RoomAvailabilityModel ra SET ra.status = :status, ra.lockId = NULL WHERE ra.lockId = :lockId")
    int updateStatusByLockId(@Param("lockId") String lockId, @Param("status") RoomStatus status);

    @Modifying
    @Query("UPDATE RoomAvailabilityModel ra SET ra.status = 'AVAILABLE', ra.lockId = NULL, ra.lockExpirationTime = NULL WHERE ra.lockId = :lockId")
    int releaseLocksById(@Param("lockId") String lockId);

    @Modifying
    @Query("UPDATE RoomAvailabilityModel ra SET ra.status = 'AVAILABLE', ra.lockId = NULL, ra.lockExpirationTime = NULL " +
            "WHERE ra.lockExpirationTime < :now AND ra.status = 'TEMPORARILY_LOCKED'")
    int releaseExpiredLocks(@Param("now") LocalDateTime now);
}
