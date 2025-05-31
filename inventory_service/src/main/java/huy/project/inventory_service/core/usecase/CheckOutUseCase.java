package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.core.domain.dto.request.CheckOutRequest;
import huy.project.inventory_service.core.domain.dto.response.CheckOutResponse;
import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.core.domain.exception.InvalidRequestException;
import huy.project.inventory_service.core.port.IRoomAvailabilityPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckOutUseCase {
    private final IRoomAvailabilityPort roomAvailabilityPort;

    public CheckOutResponse checkOut(CheckOutRequest request) {
        try {
            log.info("Processing check-out request for booking, id: {}", request.getBookingId());

            validateCheckOutRequest(request);

            List<RoomAvailability> roomAvailabilities = roomAvailabilityPort.getRoomsByBookingId(request.getBookingId());
            if (CollectionUtils.isEmpty(roomAvailabilities)) {
                throw new InvalidRequestException("No rooms found for booking ID: " + request.getBookingId());
            }

            validateCanCheckOut(roomAvailabilities, request);

            var checkOutRoom = roomAvailabilities.stream()
                    .filter(room -> room.getDate().equals(request.getCheckOutDate()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidRequestException("No room found for check-out date: " + request.getCheckOutDate()));
            checkOutRoom.checkOut();

            roomAvailabilityPort.saveAll(List.of(checkOutRoom));

            log.info("Successfully checked out guests for booking, id: {}", request.getBookingId());
            return CheckOutResponse.builder()
                    .success(true)
                    .message("Check-out successful for booking ID: " + request.getBookingId())
                    .checkOutTime(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error while checking out guests for booking, id: {}", request.getBookingId(), e);
            return CheckOutResponse.builder()
                    .success(false)
                    .message("Check-out failed for booking ID: " + request.getBookingId() + ". Error: " + e.getMessage())
                    .build();
        }
    }

    private void validateCheckOutRequest(CheckOutRequest request) {
        if (request.getBookingId() == null || request.getBookingId() <= 0) {
            throw new InvalidRequestException("Invalid booking ID: " + request.getBookingId());
        }
        if (request.getCheckOutDate() == null) {
            throw new InvalidRequestException("Check-out date cannot be null.");
        }
        if (request.getGuestId() == null || request.getGuestId() <= 0) {
            throw new InvalidRequestException("Invalid guest ID: " + request.getGuestId());
        }
    }

    private void validateCanCheckOut(List<RoomAvailability> roomAvailabilities, CheckOutRequest request) {
        for (var room : roomAvailabilities) {
            if (room.getStatus().equals(RoomStatus.CHECKED_OUT)) {
                throw new InvalidRequestException("Booking with ID " + request.getBookingId() + " has already been checked out.");
            }
            if (!room.getGuestId().equals(request.getGuestId())) {
                throw new InvalidRequestException("Guest ID " + request.getGuestId() + " is not authorized to check-out.");
            }
        }
    }

}
