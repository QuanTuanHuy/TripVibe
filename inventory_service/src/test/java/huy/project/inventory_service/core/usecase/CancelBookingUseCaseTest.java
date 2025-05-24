package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.core.domain.dto.request.CancelBookingRequest;
import huy.project.inventory_service.core.domain.dto.response.CancelBookingResponse;
import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.core.port.IRoomAvailabilityPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelBookingUseCaseTest {

    @Mock
    private IRoomAvailabilityPort roomAvailabilityPort;

    @InjectMocks
    private CancelBookingUseCase cancelBookingUseCase;
    @Captor
    private ArgumentCaptor<List<RoomAvailability>> roomAvailabilitiesCaptor;

    private final Long BOOKING_ID = 123L;
    private final Long USER_ID = 456L;
    private final Long GUEST_ID = 456L;
    private final Long DIFFERENT_USER_ID = 789L;

    private CancelBookingRequest validRequest;
    private List<RoomAvailability> validRoomAvailabilities;

    @BeforeEach
    void setUp() {
        // Set up valid request
        validRequest = new CancelBookingRequest();
        validRequest.setBookingId(BOOKING_ID);
        validRequest.setUserId(USER_ID);

        // Set up valid room availabilities
        validRoomAvailabilities = new ArrayList<>();

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        RoomAvailability room1 = createRoomAvailability(1L, BOOKING_ID, GUEST_ID, tomorrow, RoomStatus.BOOKED);
        RoomAvailability room2 = createRoomAvailability(2L, BOOKING_ID, GUEST_ID, tomorrow.plusDays(1), RoomStatus.BOOKED);

        validRoomAvailabilities.add(room1);
        validRoomAvailabilities.add(room2);
    }

    @Test
    @DisplayName("Should successfully cancel a booking when all conditions are met")
    void cancelBooking_Success() {
        // Arrange
        when(roomAvailabilityPort.getRoomsByBookingId(BOOKING_ID)).thenReturn(validRoomAvailabilities);
        when(roomAvailabilityPort.saveAll(anyList())).thenReturn(validRoomAvailabilities);

        // Act
        CancelBookingResponse response = cancelBookingUseCase.cancelBooking(validRequest);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(BOOKING_ID, response.getBookingId());
        assertNotNull(response.getCanceledAt());
        assertTrue(response.getErrors().isEmpty());

        // Verify room statuses were updated
        verify(roomAvailabilityPort).saveAll(roomAvailabilitiesCaptor.capture());
        List<RoomAvailability> updatedRooms = roomAvailabilitiesCaptor.getValue();

        assertEquals(validRoomAvailabilities.size(), updatedRooms.size());
        for (RoomAvailability room : updatedRooms) {
            assertEquals(RoomStatus.AVAILABLE, room.getStatus());
            assertNull(room.getBookingId());
        }
    }

    @Test
    @DisplayName("Should throw exception when booking ID is null")
    void cancelBooking_NullBookingId() {
        // Arrange
        CancelBookingRequest request = new CancelBookingRequest();
        request.setUserId(USER_ID);

        // Act & Assert
        CancelBookingResponse response = cancelBookingUseCase.cancelBooking(request);

        assertFalse(response.isSuccess());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("Booking ID is required"));

        verify(roomAvailabilityPort, never()).getRoomsByBookingId(any());
        verify(roomAvailabilityPort, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should throw exception when user ID is null")
    void cancelBooking_NullUserId() {
        // Arrange
        CancelBookingRequest request = new CancelBookingRequest();
        request.setBookingId(BOOKING_ID);

        // Act & Assert
        CancelBookingResponse response = cancelBookingUseCase.cancelBooking(request);

        assertFalse(response.isSuccess());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("User ID is required"));

        verify(roomAvailabilityPort, never()).getRoomsByBookingId(any());
        verify(roomAvailabilityPort, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should fail when no rooms are found for the booking")
    void cancelBooking_NoRoomsFound() {
        // Arrange
        when(roomAvailabilityPort.getRoomsByBookingId(BOOKING_ID)).thenReturn(Collections.emptyList());

        // Act
        CancelBookingResponse response = cancelBookingUseCase.cancelBooking(validRequest);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("No rooms found"));

        verify(roomAvailabilityPort).getRoomsByBookingId(BOOKING_ID);
        verify(roomAvailabilityPort, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should fail when user is not authorized to cancel booking")
    void cancelBooking_Unauthorized() {
        // Arrange
        CancelBookingRequest request = new CancelBookingRequest();
        request.setBookingId(BOOKING_ID);
        request.setUserId(DIFFERENT_USER_ID); // Different user than the one who made the booking

        when(roomAvailabilityPort.getRoomsByBookingId(BOOKING_ID)).thenReturn(validRoomAvailabilities);

        // Act
        CancelBookingResponse response = cancelBookingUseCase.cancelBooking(request);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("not authorized"));

        verify(roomAvailabilityPort).getRoomsByBookingId(BOOKING_ID);
        verify(roomAvailabilityPort, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should fail when booking is in the past")
    void cancelBooking_PastBooking() {
        // Arrange
        List<RoomAvailability> pastBookings = new ArrayList<>();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        pastBookings.add(createRoomAvailability(1L, BOOKING_ID, GUEST_ID, yesterday, RoomStatus.BOOKED));

        when(roomAvailabilityPort.getRoomsByBookingId(BOOKING_ID)).thenReturn(pastBookings);

        // Act
        CancelBookingResponse response = cancelBookingUseCase.cancelBooking(validRequest);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("past"));

        verify(roomAvailabilityPort).getRoomsByBookingId(BOOKING_ID);
        verify(roomAvailabilityPort, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should fail when room status is not BOOKED")
    void cancelBooking_NotBookedStatus() {
        // Arrange
        List<RoomAvailability> notBookedRooms = new ArrayList<>();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        notBookedRooms.add(createRoomAvailability(1L, BOOKING_ID, GUEST_ID, tomorrow, RoomStatus.CHECKED_IN));

        when(roomAvailabilityPort.getRoomsByBookingId(BOOKING_ID)).thenReturn(notBookedRooms);

        // Act
        CancelBookingResponse response = cancelBookingUseCase.cancelBooking(validRequest);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("cannot be canceled"));

        verify(roomAvailabilityPort).getRoomsByBookingId(BOOKING_ID);
        verify(roomAvailabilityPort, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should handle exceptions thrown during cancellation")
    void cancelBooking_ExceptionHandling() {
        // Arrange
        when(roomAvailabilityPort.getRoomsByBookingId(BOOKING_ID)).thenThrow(new RuntimeException("Database error"));

        // Act
        CancelBookingResponse response = cancelBookingUseCase.cancelBooking(validRequest);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(1, response.getErrors().size());
        assertEquals("Database error", response.getErrors().getFirst());

        verify(roomAvailabilityPort).getRoomsByBookingId(BOOKING_ID);
        verify(roomAvailabilityPort, never()).saveAll(any());
    }

    private RoomAvailability createRoomAvailability(Long roomId, Long bookingId, Long guestId, LocalDate date, RoomStatus status) {
        RoomAvailability roomAvailability = new RoomAvailability();
        roomAvailability.setId(roomId);
        roomAvailability.setRoomId(roomId);
        roomAvailability.setBookingId(bookingId);
        roomAvailability.setGuestId(guestId);
        roomAvailability.setDate(date);
        roomAvailability.setStatus(status);
        return roomAvailability;
    }
}