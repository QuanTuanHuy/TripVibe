package huy.project.inventory_service.core.port;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.core.domain.entity.RoomAvailability;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IRoomAvailabilityPort {
    List<RoomAvailability> saveAll(List<RoomAvailability> availabilities);

    List<RoomAvailability> getAvailabilitiesByRoomIdAndDateRange(Long roomId, LocalDate startDate, LocalDate endDate);

    RoomAvailability getAvailabilityByRoomIdAndDate(Long roomId, LocalDate date);
    
    /**
     * Releases locks for all room availabilities with the specified lock ID.
     * 
     * @param lockId the lock ID to release
     * @return number of records updated
     */
    int releaseLocksById(String lockId);
    
    /**
     * Finds all room availabilities by lock ID.
     * 
     * @param lockId the lock ID to search for
     * @return list of room availabilities with the specified lock ID
     */
    List<RoomAvailability> findByLockId(String lockId);
    
    /**
     * Updates the status of room availabilities with the specified lock ID.
     * 
     * @param lockId the lock ID to update
     * @param status the new status to set
     * @return number of records updated
     */
    int updateStatusByLockId(String lockId, RoomStatus status);

    /**
     * Releases expired locks for room availabilities.
     *
     * @param now the current time to check against
     * @return number of records updated
     */
    int releaseExpiredLocks(LocalDateTime now);
}
