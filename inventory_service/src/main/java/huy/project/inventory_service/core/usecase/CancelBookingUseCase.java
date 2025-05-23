package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.core.domain.dto.request.CancelBookingRequest;
import huy.project.inventory_service.core.domain.dto.response.CancelBookingResponse;
import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.core.domain.exception.InvalidRequestException;
import huy.project.inventory_service.core.port.IRoomAvailabilityPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelBookingUseCase {
    private final IRoomAvailabilityPort roomAvailabilityPort;

    @Transactional(rollbackFor = Exception.class)
    public CancelBookingResponse cancelBooking(CancelBookingRequest request) {
        try {
            validateCancellationRequest(request);

            List<RoomAvailability> rooms = roomAvailabilityPort.getRoomsByBookingId(request.getBookingId());

            validateAuthorization(request, rooms);

            validateBookingCanBeCanceled(rooms);

            rooms = rooms.stream()
                    .peek(RoomAvailability::cancelBooking).toList();

            roomAvailabilityPort.saveAll(rooms);

            return CancelBookingResponse.builder()
                    .success(true)
                    .bookingId(request.getBookingId())
                    .canceledAt(LocalDateTime.now())
                    .errors(Collections.emptyList())
                    .build();

        } catch (Exception e) {
            log.error("Error while canceling booking: {}", e.getMessage(), e);
            return CancelBookingResponse.builder()
                    .success(false)
                    .bookingId(request.getBookingId())
                    .canceledAt(LocalDateTime.now())
                    .errors(Collections.singletonList(e.getMessage()))
                    .build();
        }

    }

    private void validateBookingCanBeCanceled(List<RoomAvailability> rooms) {
        LocalDate now = LocalDate.now();
        boolean canBeCanceled = rooms.stream()
                .allMatch(room -> room.getDate().isAfter(now) &&
                        room.getStatus().equals(RoomStatus.BOOKED));
        if (!canBeCanceled) {
            throw new InvalidRequestException("Booking cannot be canceled because it is in the past");
        }
    }

    private void validateAuthorization(CancelBookingRequest request, List<RoomAvailability> rooms) {
        if (rooms.isEmpty()) {
            throw new InvalidRequestException("No rooms found for the provided booking ID");
        }
        boolean isAuthorized = rooms.stream()
                .allMatch(room -> room.getGuestId().equals(request.getUserId()));
        if (!isAuthorized) {
            throw new InvalidRequestException("User is not authorized to cancel this booking");
        }
        // need to be checked if the owner of accommodation cancels the booking
    }

    private void validateCancellationRequest(CancelBookingRequest request) {
        if (request.getBookingId() == null) {
            throw new InvalidRequestException("Booking ID is required");
        }

        if (request.getUserId() == null) {
            throw new InvalidRequestException("User ID is required");
        }
    }
}
