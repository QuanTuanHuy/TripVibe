package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.core.domain.dto.request.CheckInRequest;
import huy.project.inventory_service.core.domain.dto.response.CheckInResponse;
import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.core.domain.exception.InvalidRequestException;
import huy.project.inventory_service.core.domain.exception.NotFoundException;
import huy.project.inventory_service.core.port.IRoomAvailabilityPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckInUseCase {
    private final IRoomAvailabilityPort roomAvailabilityPort;

    public CheckInResponse checkIn(CheckInRequest request) {
        try {
            log.info("Processing check-in request for booking, id: {}", request.getBookingId());
            
            validateCheckInRequest(request);

            List<RoomAvailability> rooms = roomAvailabilityPort.getRoomsByBookingId(request.getBookingId());
            if (CollectionUtils.isEmpty(rooms)) {
                throw new NotFoundException("No rooms found for booking ID: " + request.getBookingId());
            }

            validateCanCheckIn(rooms, request);

            rooms.forEach(room -> {
                if (room.getDate().equals(request.getCheckInDate())) {
                    room.checkIn();
                } else {
                    room.setStatus(RoomStatus.OCCUPIED);
                }
                room.setGuestCount(request.getGuestCount());
            });

            roomAvailabilityPort.saveAll(rooms);

            log.info("Successfully checked-in guests for booking, id: {}", request.getBookingId());
            return CheckInResponse.builder()
                    .success(true)
                    .message("Check-in successful for booking ID: " + request.getBookingId())
                    .checkInTime(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("Error while checking-in guests for booking, id: {}", request.getBookingId(), e);
            return CheckInResponse.builder()
                    .success(false)
                    .message("Check-in failed for booking ID: " + request.getBookingId() + ". Error: " + e.getMessage())
                    .checkInTime(LocalDateTime.now())
                    .build();
        }
    }

    private void validateCanCheckIn(List<RoomAvailability> rooms, CheckInRequest request) {
//        LocalDate today = LocalDate.now();
//        if (today.equals(request.getCheckInDate())) {
//            throw new InvalidRequestException("Check-in date must be today: " + today);
//        }

        for (RoomAvailability room : rooms) {
            if (room.getStatus() != RoomStatus.BOOKED) {
                throw new InvalidRequestException("Room with ID " + room.getId() + " is not in booked status.");
            }
            if (!room.getGuestId().equals(request.getGuestId())) {
                throw new InvalidRequestException("Guest ID " + request.getGuestId() + " is not authorized to check-in.");
            }
        }

        LocalDate minDate = rooms.stream()
                .map(RoomAvailability::getDate)
                .min(LocalDate::compareTo)
                .orElseThrow(() -> new NotFoundException("No valid check-in date found for booking ID: " + request.getBookingId()));

        LocalDate maxDate = rooms.stream()
                .map(RoomAvailability::getDate)
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new NotFoundException("No valid check-out date found for booking ID: " + request.getBookingId()));

        if (request.getCheckInDate().isBefore(minDate) || request.getCheckInDate().isAfter(maxDate)) {
            throw new InvalidRequestException("Check-in date " + request.getCheckInDate() + " is not within the booking dates: " + minDate + " to " + maxDate);
        }
    }

    private void validateCheckInRequest(CheckInRequest request) {
        if (request.getBookingId() == null || request.getBookingId() <= 0) {
            throw new IllegalArgumentException("Invalid booking ID: " + request.getBookingId());
        }
        if (request.getGuestId() == null || request.getGuestId() <= 0) {
            throw new IllegalArgumentException("Invalid guest ID: " + request.getGuestId());
        }
        if (request.getCheckInDate() == null) {
            throw new IllegalArgumentException("Check-in date cannot be null");
        }
        if (request.getGuestCount() <= 0) {
            throw new IllegalArgumentException("Guest count must be greater than zero");
        }
    }
}
