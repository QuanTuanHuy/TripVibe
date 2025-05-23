package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.core.domain.dto.request.ConfirmBookingRequest;
import huy.project.inventory_service.core.domain.dto.request.ConfirmBookingResponse;
import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.core.port.ILockPort;
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
class ConfirmBookingUseCaseTest {

    @Mock
    private ILockPort lockPort;

    @Mock
    private IRoomAvailabilityPort roomAvailabilityPort;

    @InjectMocks
    private ConfirmBookingUseCase confirmBookingUseCase;

    @Captor
    private ArgumentCaptor<List<RoomAvailability>> roomAvailabilitiesCaptor;

    private final Long BOOKING_ID = 123L;
    private final String LOCK_ID = "lock-456";
    private final Long ROOM_ID_1 = 111L;
    private final Long ROOM_ID_2 = 222L;

    private ConfirmBookingRequest validRequest;
    private List<RoomAvailability> lockedRoomAvailabilities;

    @BeforeEach
    void setUp() {
        // Set up valid request
        validRequest = new ConfirmBookingRequest();
        validRequest.setBookingId(BOOKING_ID);
        validRequest.setLockId(LOCK_ID);

        // Set up temporarily locked room availabilities
        lockedRoomAvailabilities = new ArrayList<>();

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        RoomAvailability room1 = createRoomAvailability(ROOM_ID_1, null, tomorrow, RoomStatus.TEMPORARILY_LOCKED);
        room1.setLockId(LOCK_ID);

        LocalDate dayAfterTomorrow = tomorrow.plusDays(1);
        RoomAvailability room2 = createRoomAvailability(ROOM_ID_2, null, dayAfterTomorrow, RoomStatus.TEMPORARILY_LOCKED);
        room2.setLockId(LOCK_ID);

        lockedRoomAvailabilities.add(room1);
        lockedRoomAvailabilities.add(room2);
    }

    @Test
    @DisplayName("Should successfully confirm booking when all conditions are met")
    void confirmBooking_Success() {
        // Arrange
        when(roomAvailabilityPort.findByLockId(LOCK_ID)).thenReturn(lockedRoomAvailabilities);
        when(roomAvailabilityPort.saveAll(anyList())).thenReturn(lockedRoomAvailabilities);
        // No need to stub void method with return value
        doNothing().when(lockPort).releaseLock(LOCK_ID);

        // Act
        ConfirmBookingResponse response = confirmBookingUseCase.confirmBooking(validRequest);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(BOOKING_ID, response.getBookingId());
        assertNotNull(response.getConfirmedAt());
        assertTrue(response.getErrors().isEmpty());

        // Verify room statuses were updated
        verify(roomAvailabilityPort).saveAll(roomAvailabilitiesCaptor.capture());
        List<RoomAvailability> updatedRooms = roomAvailabilitiesCaptor.getValue();

        assertEquals(lockedRoomAvailabilities.size(), updatedRooms.size());
        for (RoomAvailability room : updatedRooms) {
            assertEquals(RoomStatus.BOOKED, room.getStatus());
            assertEquals(BOOKING_ID, room.getBookingId());
        }

        // Verify lock was released
        verify(lockPort).releaseLock(LOCK_ID);
    }

    @Test
    @DisplayName("Should fail when booking ID is null")
    void confirmBooking_NullBookingId() {
        // Arrange
        ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setLockId(LOCK_ID);

        // Act
        ConfirmBookingResponse response = confirmBookingUseCase.confirmBooking(request);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("ID must not be null"));

        // Verify no interactions with ports
        verify(roomAvailabilityPort, never()).findByLockId(any());
        verify(roomAvailabilityPort, never()).saveAll(any());
        verify(lockPort, never()).releaseLock(any());
    }

    @Test
    @DisplayName("Should fail when lock ID is null")
    void confirmBooking_NullLockId() {
        // Arrange
        ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setBookingId(BOOKING_ID);

        // Act
        ConfirmBookingResponse response = confirmBookingUseCase.confirmBooking(request);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("ID must not be null"));

        // Verify no interactions with ports
        verify(roomAvailabilityPort, never()).findByLockId(any());
        verify(roomAvailabilityPort, never()).saveAll(any());
        verify(lockPort, never()).releaseLock(any());
    }

    @Test
    @DisplayName("Should fail when no locked rooms are found")
    void confirmBooking_NoRoomsFound() {
        // Arrange
        when(roomAvailabilityPort.findByLockId(LOCK_ID)).thenReturn(Collections.emptyList());

        // Act
        ConfirmBookingResponse response = confirmBookingUseCase.confirmBooking(validRequest);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("No locked rooms found"));

        // Verify no other actions were taken
        verify(roomAvailabilityPort).findByLockId(LOCK_ID);
        verify(roomAvailabilityPort, never()).saveAll(any());
        verify(lockPort, never()).releaseLock(any());
    }

    @Test
    @DisplayName("Should fail when rooms are not in TEMPORARILY_LOCKED status")
    void confirmBooking_NotTemporarilyLockedStatus() {
        // Arrange
        List<RoomAvailability> invalidRooms = new ArrayList<>();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        RoomAvailability invalidRoom = createRoomAvailability(ROOM_ID_1, null, tomorrow, RoomStatus.AVAILABLE);
        invalidRoom.setLockId(LOCK_ID);
        invalidRooms.add(invalidRoom);

        when(roomAvailabilityPort.findByLockId(LOCK_ID)).thenReturn(invalidRooms);

        // Act
        ConfirmBookingResponse response = confirmBookingUseCase.confirmBooking(validRequest);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("not in a valid state"));

        // Verify no other actions were taken
        verify(roomAvailabilityPort).findByLockId(LOCK_ID);
        verify(roomAvailabilityPort, never()).saveAll(any());
        verify(lockPort, never()).releaseLock(any());
    }

    @Test
    @DisplayName("Should handle exceptions thrown during confirmation")
    void confirmBooking_ExceptionHandling() {
        // Arrange
        when(roomAvailabilityPort.findByLockId(LOCK_ID)).thenThrow(new RuntimeException("Database error"));

        // Act
        ConfirmBookingResponse response = confirmBookingUseCase.confirmBooking(validRequest);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(1, response.getErrors().size());
        assertEquals("Database error", response.getErrors().getFirst());

        // Verify no other actions were taken
        verify(roomAvailabilityPort).findByLockId(LOCK_ID);
        verify(roomAvailabilityPort, never()).saveAll(any());
        verify(lockPort, never()).releaseLock(any());
    }

    private RoomAvailability createRoomAvailability(Long roomId, Long bookingId, LocalDate date, RoomStatus status) {
        RoomAvailability roomAvailability = new RoomAvailability();
        roomAvailability.setId(roomId);
        roomAvailability.setRoomId(roomId);
        roomAvailability.setBookingId(bookingId);
        roomAvailability.setDate(date);
        roomAvailability.setStatus(status);
        return roomAvailability;
    }
}