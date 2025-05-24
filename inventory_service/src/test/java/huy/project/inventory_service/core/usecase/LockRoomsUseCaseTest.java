package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.constant.RoomStatus;
import huy.project.inventory_service.core.domain.dto.request.AccommodationLockRequest;
import huy.project.inventory_service.core.domain.dto.request.UnitLockRequest;
import huy.project.inventory_service.core.domain.dto.response.AccommodationLockResponse;
import huy.project.inventory_service.core.domain.entity.Room;
import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.domain.exception.InvalidRequestException;
import huy.project.inventory_service.core.port.ILockPort;
import huy.project.inventory_service.core.port.IRoomAvailabilityPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LockRoomsUseCaseTest {

    @Mock
    private GetUnitUseCase getUnitUseCase;

    @Mock
    private IRoomAvailabilityPort roomAvailabilityPort;

    @Mock
    private ILockPort lockPort;

    @InjectMocks
    private LockRoomsUseCase lockRoomsUseCase;

    @Captor
    private ArgumentCaptor<List<RoomAvailability>> availabilityListCaptor;

    @Captor
    private ArgumentCaptor<String> lockIdCaptor;

    private AccommodationLockRequest request;
    private Unit unit;
    private List<Room> rooms;
    private List<RoomAvailability> availabilities;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        // Setup common test data
        startDate = LocalDate.now();
        endDate = startDate.plusDays(3);

        // Create unit lock request
        UnitLockRequest unitLockRequest = new UnitLockRequest();
        unitLockRequest.setUnitId(1L);
        unitLockRequest.setQuantity(2);
        unitLockRequest.setStartDate(startDate);
        unitLockRequest.setEndDate(endDate);

        // Create accommodation lock request
        request = new AccommodationLockRequest();
        request.setAccommodationId(100L);
        request.setUserId(1000L);
        request.setUnitLockRequests(Collections.singletonList(unitLockRequest));

        // Create unit with rooms
        unit = Unit.builder()
                .id(1L)
                .accommodationId(100L)
                .unitNameId(10L)
                .unitName("Deluxe Suite")
                .basePrice(new BigDecimal("200.00"))
                .quantity(3)
                .build();

        // Create rooms for the unit
        rooms = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            rooms.add(Room.builder()
                    .id(101L + i)
                    .unitId(1L)
                    .roomNumber("U1-" + (i + 1))
                    .name("Deluxe Suite " + (i + 1))
                    .basePrice(new BigDecimal("200.00"))
                    .build());
        }
        unit.setRooms(rooms);

        // Create room availabilities
        availabilities = new ArrayList<>();
        for (Room room : rooms) {
            LocalDate current = startDate;
            while (!current.isAfter(endDate)) {
                availabilities.add(RoomAvailability.builder()
                        .id(1000L + availabilities.size())
                        .roomId(room.getId())
                        .date(current)
                        .status(RoomStatus.AVAILABLE)
                        .price(room.getBasePrice())
                        .basePrice(room.getBasePrice())
                        .build());
                current = current.plusDays(1);
            }
        }
    }

    @Test
    void execute_shouldSuccessfullyLockRooms() {
        // Arrange
        when(getUnitUseCase.getUnitById(anyLong())).thenReturn(unit);
        when(roomAvailabilityPort.getAvailabilitiesByRoomIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenAnswer(invocation -> {
                    Long roomId = invocation.getArgument(0);
                    return availabilities.stream()
                            .filter(a -> a.getRoomId().equals(roomId))
                            .toList();
                });
        when(roomAvailabilityPort.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(lockPort.acquireLock(anyString(), anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        // Act
        AccommodationLockResponse response = lockRoomsUseCase.execute(request);

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.getLockId());
        assertTrue(response.getErrors().isEmpty());

        // Verify that the distributed lock was acquired and released
        verify(lockPort).acquireLock(matches("inventory_lock:100"), eq(5L), eq(30L), eq(TimeUnit.SECONDS));
        verify(lockPort).releaseLock(matches("inventory_lock:100"));

        // Verify that the unit was fetched
        verify(getUnitUseCase).getUnitById(1L);

        // Verify that room availabilities were fetched for each room
        verify(roomAvailabilityPort, times(3)).getAvailabilitiesByRoomIdAndDateRange(
                anyLong(), eq(startDate), eq(endDate));

        // Verify that room availabilities were saved
        verify(roomAvailabilityPort).saveAll(availabilityListCaptor.capture());
        List<RoomAvailability> capturedAvailabilities = availabilityListCaptor.getValue();

        // Should be 2 rooms * 4 days = 8 availabilities
        assertEquals(8, capturedAvailabilities.size());

        // Check that all captured availabilities have TEMPORARILY_LOCKED status and correct lockId
        for (RoomAvailability availability : capturedAvailabilities) {
            assertEquals(RoomStatus.TEMPORARILY_LOCKED, availability.getStatus());
            assertNotNull(availability.getLockId());
            assertTrue(availability.getLockId().startsWith("100_1000_"));
        }

        // Verify that the main lock was created
        verify(lockPort).acquireLock(lockIdCaptor.capture(), eq(5L), eq(1800L), eq(TimeUnit.SECONDS));
        String capturedLockId = lockIdCaptor.getValue();
        assertTrue(capturedLockId.startsWith("100_1000_"));
    }

    @Test
    void execute_whenDistributedLockCannotBeAcquired_shouldReturnFailure() {
        // Arrange
        when(lockPort.acquireLock(matches("inventory_lock:100"), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(false);

        // Act
        AccommodationLockResponse response = lockRoomsUseCase.execute(request);

        // Assert
        assertFalse(response.isSuccess());
        assertNull(response.getLockId());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("Could not acquire lock"));

        // Verify that no other operations were performed
        verify(getUnitUseCase, never()).getUnitById(anyLong());
        verify(roomAvailabilityPort, never()).getAvailabilitiesByRoomIdAndDateRange(
                anyLong(), any(LocalDate.class), any(LocalDate.class));
        verify(roomAvailabilityPort, never()).saveAll(anyList());
    }

    @Test
    void execute_withInvalidRequest_noAccommodationId_shouldThrowException() {
        // Arrange
        request.setAccommodationId(null);

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> lockRoomsUseCase.execute(request)
        );

        assertEquals("Accommodation ID is required", exception.getMessage());
    }

    @Test
    void execute_withInvalidRequest_noUnitRequests_shouldThrowException() {
        // Arrange
        request.setUnitLockRequests(null);

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> lockRoomsUseCase.execute(request)
        );

        assertEquals("At least one unit request is required", exception.getMessage());
    }

    @Test
    void execute_withInvalidRequest_unitRequestNoUnitId_shouldThrowException() {
        // Arrange
        request.getUnitLockRequests().getFirst().setUnitId(null);

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> lockRoomsUseCase.execute(request)
        );

        assertEquals("Unit ID is required", exception.getMessage());
    }

    @Test
    void execute_withInvalidRequest_unitRequestNoQuantity_shouldThrowException() {
        // Arrange
        request.getUnitLockRequests().getFirst().setQuantity(null);

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> lockRoomsUseCase.execute(request)
        );

        assertEquals("Quantity must be a positive number", exception.getMessage());
    }

    @Test
    void execute_withInvalidRequest_unitRequestZeroQuantity_shouldThrowException() {
        // Arrange
        request.getUnitLockRequests().getFirst().setQuantity(0);

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> lockRoomsUseCase.execute(request)
        );

        assertEquals("Quantity must be a positive number", exception.getMessage());
    }

    @Test
    void execute_withInvalidRequest_unitRequestNoStartDate_shouldThrowException() {
        // Arrange
        request.getUnitLockRequests().getFirst().setStartDate(null);

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> lockRoomsUseCase.execute(request)
        );

        assertEquals("Start date is required", exception.getMessage());
    }

    @Test
    void execute_withInvalidRequest_unitRequestNoEndDate_shouldThrowException() {
        // Arrange
        request.getUnitLockRequests().getFirst().setEndDate(null);

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> lockRoomsUseCase.execute(request)
        );

        assertEquals("End date is required", exception.getMessage());
    }

    @Test
    void execute_withInvalidRequest_startDateAfterEndDate_shouldThrowException() {
        // Arrange
        request.getUnitLockRequests().getFirst().setStartDate(LocalDate.now().plusDays(5));
        request.getUnitLockRequests().getFirst().setEndDate(LocalDate.now());

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> lockRoomsUseCase.execute(request)
        );

        assertEquals("Start date must be before end date", exception.getMessage());
    }

    @Test
    void execute_withInvalidRequest_startDateInPast_shouldThrowException() {
        // Arrange
        request.getUnitLockRequests().getFirst().setStartDate(LocalDate.now().minusDays(1));

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> lockRoomsUseCase.execute(request)
        );

        assertEquals("Start date cannot be in the past", exception.getMessage());
    }

    @Test
    void execute_whenUnitNotFound_shouldReturnFailureResponse() {
        // Arrange
        when(getUnitUseCase.getUnitById(anyLong())).thenReturn(null);
        when(lockPort.acquireLock(matches("inventory_lock:100"), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);

        // Act
        AccommodationLockResponse response = lockRoomsUseCase.execute(request);

        // Assert
        assertFalse(response.isSuccess());
        assertNull(response.getLockId());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("Unit not found"));

        // Verify that the distributed lock was released
        verify(lockPort).releaseLock(matches("inventory_lock:100"));
    }

    @Test
    void execute_whenNoRoomsAvailable_shouldReturnFailureResponse() {
        // Arrange
        unit.setRooms(Collections.emptyList());

        when(getUnitUseCase.getUnitById(anyLong())).thenReturn(unit);
        when(lockPort.acquireLock(matches("inventory_lock:100"), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);

        // Act
        AccommodationLockResponse response = lockRoomsUseCase.execute(request);

        // Assert
        assertFalse(response.isSuccess());
        assertNull(response.getLockId());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("No rooms available for unit"));

        // Verify that the distributed lock was released
        verify(lockPort).releaseLock(matches("inventory_lock:100"));
    }

    @Test
    void execute_whenNotEnoughRooms_shouldReturnFailureResponse() {
        // Arrange
        request.getUnitLockRequests().getFirst().setQuantity(5); // More than available

        when(getUnitUseCase.getUnitById(anyLong())).thenReturn(unit);
        when(lockPort.acquireLock(matches("inventory_lock:100"), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);

        // Act
        AccommodationLockResponse response = lockRoomsUseCase.execute(request);

        // Assert
        assertFalse(response.isSuccess());
        assertNull(response.getLockId());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("Requested 5 rooms but only 3 available"));

        // Verify that the distributed lock was released
        verify(lockPort).releaseLock(matches("inventory_lock:100"));
    }

    @Test
    void execute_whenNotEnoughAvailableRoomsForDateRange_shouldReturnFailureResponse() {
        // Arrange
        // Make one room unavailable for the requested dates
        for (RoomAvailability availability : availabilities) {
            if (availability.getRoomId().equals(101L)) {
                availability.setStatus(RoomStatus.BOOKED);
            }
        }

        when(getUnitUseCase.getUnitById(anyLong())).thenReturn(unit);
        when(roomAvailabilityPort.getAvailabilitiesByRoomIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenAnswer(invocation -> {
                    Long roomId = invocation.getArgument(0);
                    return availabilities.stream()
                            .filter(a -> a.getRoomId().equals(roomId))
                            .toList();
                });
        when(lockPort.acquireLock(matches("inventory_lock:100"), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);

        // Act
        AccommodationLockResponse response = lockRoomsUseCase.execute(request);

        // Assert
        assertFalse(response.isSuccess());
        assertNull(response.getLockId());
        assertEquals(1, response.getErrors().size());

        // Verify that the distributed lock was released
        verify(lockPort).releaseLock(matches("inventory_lock:100"));
    }

    @Test
    void execute_whenRedisFinalLockFails_shouldReleaseRoomLocks() {
        // Arrange
        when(getUnitUseCase.getUnitById(anyLong())).thenReturn(unit);
        when(roomAvailabilityPort.getAvailabilitiesByRoomIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenAnswer(invocation -> {
                    Long roomId = invocation.getArgument(0);
                    return availabilities.stream()
                            .filter(a -> a.getRoomId().equals(roomId))
                            .toList();
                });
        when(roomAvailabilityPort.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(roomAvailabilityPort.updateStatusByLockId(anyString(), any(RoomStatus.class))).thenReturn(8);

        // First call is for the distributed lock (succeed), second call is for the final lock (fail)
        when(lockPort.acquireLock(anyString(), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenAnswer(invocation -> {
                    String lockKey = invocation.getArgument(0);
                    return lockKey.startsWith("inventory_lock");
                });

        // Act
        AccommodationLockResponse response = lockRoomsUseCase.execute(request);

        // Assert
        assertFalse(response.isSuccess());
        assertNull(response.getLockId());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("Failed to create lock record"));

        // Verify that room locks were released
        verify(roomAvailabilityPort).updateStatusByLockId(lockIdCaptor.capture(), eq(RoomStatus.AVAILABLE));
        String capturedLockId = lockIdCaptor.getValue();
        assertTrue(capturedLockId.startsWith("100_1000_"));

        // Verify that the distributed lock was released
        verify(lockPort).releaseLock(matches("inventory_lock:100"));
    }

    @Test
    void execute_whenExceptionOccurs_shouldReleaseRoomLocksAndDistributedLock() {
        // Arrange
        when(getUnitUseCase.getUnitById(anyLong())).thenReturn(unit);
        when(roomAvailabilityPort.getAvailabilitiesByRoomIdAndDateRange(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new RuntimeException("Database error"));
        when(roomAvailabilityPort.updateStatusByLockId(anyString(), any(RoomStatus.class))).thenReturn(0);
        when(lockPort.acquireLock(matches("inventory_lock:100"), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(true);

        // Act
        AccommodationLockResponse response = lockRoomsUseCase.execute(request);

        // Assert
        assertFalse(response.isSuccess());
        assertNull(response.getLockId());
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().getFirst().contains("Database error"));

        // Verify that room locks were released (even if there were no locks to release)
        verify(roomAvailabilityPort).updateStatusByLockId(anyString(), eq(RoomStatus.AVAILABLE));

        // Verify that the distributed lock was released
        verify(lockPort).releaseLock(matches("inventory_lock:100"));
    }
}
