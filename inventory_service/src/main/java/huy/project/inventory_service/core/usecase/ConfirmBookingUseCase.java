package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.core.port.ILockPort;
import huy.project.inventory_service.core.port.IRoomAvailabilityPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmBookingUseCase {
    private final ILockPort lockPort;
    private final IRoomAvailabilityPort roomAvailabilityPort;

    @Transactional
    public boolean confirmBooking(String lockId, Long bookingId) {
        try {
            // Get all room availabilities with this lock ID
            List<RoomAvailability> lockedAvailabilities = roomAvailabilityPort.findByLockId(lockId);

            if (lockedAvailabilities.isEmpty()) {
                log.warn("No locked rooms found for lockId: {}", lockId);
                return false;
            }

            // Update each availability to BOOKED status
            for (RoomAvailability availability : lockedAvailabilities) {
                availability.setStatus(RoomStatus.BOOKED);
                availability.setBookingId(bookingId);
                availability.setLockId(null); // Remove lock ID as it's now booked
            }

            // Save all updates
            roomAvailabilityPort.saveAll(lockedAvailabilities);

            // Release the distributed lock as it's no longer needed
            lockPort.releaseLock(lockId);

            log.info("Successfully confirmed booking with ID {} for lockId: {}", bookingId, lockId);
            return true;
        } catch (Exception e) {
            log.error("Error while confirming booking for lockId: {}", lockId, e);
            return false;
        }
    }
}
