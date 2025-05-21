package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.core.domain.dto.request.ConfirmBookingRequest;
import huy.project.inventory_service.core.domain.dto.request.ConfirmBookingResponse;
import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.core.domain.exception.InvalidRequestException;
import huy.project.inventory_service.core.domain.exception.NotFoundException;
import huy.project.inventory_service.core.port.ILockPort;
import huy.project.inventory_service.core.port.IRoomAvailabilityPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmBookingUseCase {
    private final ILockPort lockPort;
    private final IRoomAvailabilityPort roomAvailabilityPort;

    @Transactional
    public ConfirmBookingResponse confirmBooking(ConfirmBookingRequest request) {
        Long bookingId = request.getBookingId();
        String lockId = request.getLockId();

        try {
            validateConfirmationRequest(request);

            List<RoomAvailability> lockedAvailabilities = roomAvailabilityPort.findByLockId(lockId);

            validateBookingCanBeConfirmed(lockedAvailabilities);

            if (lockedAvailabilities.isEmpty()) {
                throw new NotFoundException("No locked rooms found for lockId: " + lockId);
            }

            for (RoomAvailability availability : lockedAvailabilities) {
                availability.confirmBooking(bookingId);
            }

            roomAvailabilityPort.saveAll(lockedAvailabilities);

            lockPort.releaseLock(lockId);

            log.info("Successfully confirmed booking with ID {} for lockId: {}", bookingId, lockId);
            return ConfirmBookingResponse.builder()
                    .success(true)
                    .bookingId(bookingId)
                    .confirmedAt(LocalDateTime.now())
                    .errors(List.of())
                    .build();
        } catch (Exception e) {
            log.error("Error while confirming booking for lockId: {}", lockId, e);
            return ConfirmBookingResponse.builder()
                    .success(false)
                    .bookingId(bookingId)
                    .confirmedAt(null)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    private void validateBookingCanBeConfirmed(List<RoomAvailability> lockedAvailabilities) {
        if (CollectionUtils.isEmpty(lockedAvailabilities)) {
            throw new NotFoundException("No locked rooms found");
        }
        boolean canBeConfirmed = lockedAvailabilities.stream()
                .allMatch(room -> room.getStatus().equals(RoomStatus.TEMPORARILY_LOCKED));
        if (!canBeConfirmed) {
            throw new InvalidRequestException("Booking cannot be confirmed because it is not in a valid state");
        }
    }

    private void validateConfirmationRequest(ConfirmBookingRequest request) {
        if (request.getBookingId() == null || request.getLockId() == null) {
            throw new InvalidRequestException("Booking ID and Lock ID must not be null");
        }
    }
}
