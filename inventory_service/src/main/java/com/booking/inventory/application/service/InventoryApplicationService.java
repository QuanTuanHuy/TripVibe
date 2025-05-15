package com.booking.inventory.application.service;

import com.booking.inventory.application.dto.AvailabilitySearchRequest;
import com.booking.inventory.application.dto.BookingConfirmationRequest;
import com.booking.inventory.application.dto.RoomAvailabilityDto;
import com.booking.inventory.application.dto.RoomLockRequest;
import com.booking.inventory.domain.model.RoomAvailability;
import com.booking.inventory.domain.service.RoomAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryApplicationService {
    
    private final RoomAvailabilityService roomAvailabilityService;
    
    /**
     * Search for available rooms based on search criteria
     * @param request search request
     * @return list of available rooms
     */
    public List<RoomAvailabilityDto> searchAvailability(AvailabilitySearchRequest request) {
        List<RoomAvailability> availabilities;
        
        if (request.getRoomTypeId() != null) {
            availabilities = roomAvailabilityService.getAvailableRoomsByType(
                    request.getRoomTypeId(), request.getStartDate(), request.getEndDate());
        } else {
            availabilities = roomAvailabilityService.getAvailableRooms(
                    request.getPropertyId(), request.getStartDate(), request.getEndDate());
        }
        
        return availabilities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Lock a room for temporary booking
     * @param request lock request
     * @return true if lock was successful
     */
    public boolean lockRoom(RoomLockRequest request) {
        return roomAvailabilityService.lockRoom(
                request.getRoomId(), 
                request.getStartDate(), 
                request.getEndDate(), 
                request.getSessionId());
    }
    
    /**
     * Release a room lock
     * @param sessionId client session id
     */
    public void releaseRoomLock(String sessionId) {
        roomAvailabilityService.releaseRoomLock(sessionId);
    }
    
    /**
     * Confirm a booking
     * @param request booking confirmation request
     * @return true if booking confirmation was successful
     */
    public boolean confirmBooking(BookingConfirmationRequest request) {
        return roomAvailabilityService.confirmBooking(
                request.getRoomId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getSessionId(),
                request.getBookingId());
    }
    
    /**
     * Cancel a booking
     * @param bookingId booking id
     */
    public void cancelBooking(String bookingId) {
        roomAvailabilityService.cancelBooking(bookingId);
    }
    
    /**
     * Process check-in
     * @param bookingId booking id
     */
    public void processCheckIn(String bookingId) {
        roomAvailabilityService.processCheckIn(bookingId);
    }
    
    /**
     * Process check-out
     * @param bookingId booking id
     */
    public void processCheckOut(String bookingId) {
        roomAvailabilityService.processCheckOut(bookingId);
    }
    
    /**
     * Convert RoomAvailability entity to DTO
     * @param availability room availability entity
     * @return room availability DTO
     */
    private RoomAvailabilityDto convertToDto(RoomAvailability availability) {
        return RoomAvailabilityDto.builder()
                .id(availability.getId())
                .roomId(availability.getRoom().getId())
                .roomNumber(availability.getRoom().getRoomNumber())
                .roomName(availability.getRoom().getName())
                .propertyId(availability.getRoom().getProperty().getId())
                .propertyName(availability.getRoom().getProperty().getName())
                .roomTypeId(availability.getRoom().getRoomType().getId())
                .roomTypeName(availability.getRoom().getRoomType().getName())
                .date(availability.getDate())
                .status(availability.getStatus())
                .price(availability.getPrice())
                .bookingId(availability.getBookingId())
                .build();
    }
}
