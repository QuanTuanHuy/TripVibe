package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.entity.Room;
import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.port.IRoomPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRoomUseCaseTest {

    @Mock
    private IRoomPort roomPort;

    @Mock
    private GetRoomUseCase getRoomUseCase;

    @Mock
    private CreateRoomAvailabilityUseCase createRoomAvailabilityUseCase;

    @InjectMocks
    private CreateRoomUseCase createRoomUseCase;

    @Captor
    private ArgumentCaptor<List<Room>> roomListCaptor;

    private Unit unit;
    private List<Room> existingRooms;
    private List<Room> savedRooms;

    @BeforeEach
    void setUp() {
        unit = Unit.builder()
                .id(1L)
                .accommodationId(100L)
                .unitNameId(10L)
                .unitName("Deluxe Suite")
                .basePrice(new BigDecimal("200.00"))
                .quantity(3)
                .build();

        // Setup existing rooms for the unit
        existingRooms = Collections.singletonList(
                Room.builder()
                        .id(101L)
                        .unitId(1L)
                        .roomNumber("U1-1")
                        .name("Deluxe Suite 1")
                        .basePrice(new BigDecimal("200.00"))
                        .build()
        );

        // Setup saved rooms to be returned by roomPort.saveAll
        savedRooms = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            savedRooms.add(Room.builder()
                    .id(102L + i)
                    .unitId(1L)
                    .roomNumber("U1-" + (i + 2))
                    .name("Deluxe Suite " + (i + 2))
                    .basePrice(new BigDecimal("200.00"))
                    .build());
        }
    }

    @Test
    void createRoomsForUnit_shouldCreateCorrectNumberOfRooms() {        // Arrange
        when(getRoomUseCase.getRoomsByUnitId(unit.getId())).thenReturn(existingRooms);
        when(roomPort.saveAll(anyList())).thenReturn(savedRooms);
        doNothing().when(createRoomAvailabilityUseCase).createRoomAvailability(any(Room.class), any(LocalDate.class), any(LocalDate.class));

        // Act
        createRoomUseCase.createRoomsForUnit(unit, 2);

        // Assert
        verify(getRoomUseCase).getRoomsByUnitId(unit.getId());
        verify(roomPort).saveAll(roomListCaptor.capture());

        List<Room> capturedRooms = roomListCaptor.getValue();
        assertEquals(2, capturedRooms.size());

        // Check that room numbers and names are correctly generated
        assertEquals("U1-2", capturedRooms.get(0).getRoomNumber());
        assertEquals("U1-3", capturedRooms.get(1).getRoomNumber());
        assertEquals("Deluxe Suite 2", capturedRooms.get(0).getName());
        assertEquals("Deluxe Suite 3", capturedRooms.get(1).getName());

        // Verify that base price is correctly set
        assertEquals(unit.getBasePrice(), capturedRooms.get(0).getBasePrice());
        assertEquals(unit.getBasePrice(), capturedRooms.get(1).getBasePrice());

        // Verify that createRoomAvailabilityUseCase was called for each saved room
        verify(createRoomAvailabilityUseCase, times(2)).createRoomAvailability(any(Room.class), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void createRoomsForUnit_withNoExistingRooms_shouldStartFromOne() {        // Arrange
        when(getRoomUseCase.getRoomsByUnitId(unit.getId())).thenReturn(Collections.emptyList());
        when(roomPort.saveAll(anyList())).thenReturn(savedRooms);
        doNothing().when(createRoomAvailabilityUseCase).createRoomAvailability(any(Room.class), any(LocalDate.class), any(LocalDate.class));

        // Act
        createRoomUseCase.createRoomsForUnit(unit, 2);

        // Assert
        verify(roomPort).saveAll(roomListCaptor.capture());

        List<Room> capturedRooms = roomListCaptor.getValue();
        assertEquals(2, capturedRooms.size());

        // Check that room numbers and names start from 1
        assertEquals("U1-1", capturedRooms.get(0).getRoomNumber());
        assertEquals("U1-2", capturedRooms.get(1).getRoomNumber());
        assertEquals("Deluxe Suite 1", capturedRooms.get(0).getName());
        assertEquals("Deluxe Suite 2", capturedRooms.get(1).getName());
    }

    @Test
    void createRoomsForUnit_withNullUnitId_shouldThrowIllegalArgumentException() {
        // Arrange
        Unit unitWithNullId = Unit.builder()
                .id(null)
                .accommodationId(100L)
                .unitName("Deluxe Suite")
                .basePrice(new BigDecimal("200.00"))
                .quantity(3)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createRoomUseCase.createRoomsForUnit(unitWithNullId, 2)
        );

        assertEquals("Unit ID cannot be null", exception.getMessage());
        verifyNoInteractions(getRoomUseCase, roomPort, createRoomAvailabilityUseCase);
    }

    @Test
    void createRoomsForUnit_withZeroQuantity_shouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createRoomUseCase.createRoomsForUnit(unit, 0)
        );

        assertEquals("Quantity must be greater than zero", exception.getMessage());
        verifyNoInteractions(getRoomUseCase, roomPort, createRoomAvailabilityUseCase);
    }

    @Test
    void createRoomsForUnit_withNegativeQuantity_shouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createRoomUseCase.createRoomsForUnit(unit, -1)
        );

        assertEquals("Quantity must be greater than zero", exception.getMessage());
        verifyNoInteractions(getRoomUseCase, roomPort, createRoomAvailabilityUseCase);
    }

    @Test
    void createRoomsForUnit_shouldCreateCorrectRoomAvailabilities() {
        // Arrange
        List<Room> singleSavedRoom = Collections.singletonList(
                Room.builder()
                        .id(102L)
                        .unitId(1L)
                        .roomNumber("U1-2")
                        .name("Deluxe Suite 2")
                        .basePrice(new BigDecimal("200.00"))
                        .build()
        );
        when(getRoomUseCase.getRoomsByUnitId(unit.getId())).thenReturn(existingRooms);
        when(roomPort.saveAll(anyList())).thenReturn(singleSavedRoom);

        // Use Mockito to capture the LocalDate parameters
        ArgumentCaptor<Room> roomCaptor = ArgumentCaptor.forClass(Room.class);
        ArgumentCaptor<LocalDate> startDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> endDateCaptor = ArgumentCaptor.forClass(LocalDate.class);

        doNothing().when(createRoomAvailabilityUseCase).createRoomAvailability(
                roomCaptor.capture(), startDateCaptor.capture(), endDateCaptor.capture());

        // Act
        createRoomUseCase.createRoomsForUnit(unit, 1);

        // Assert
        verify(createRoomAvailabilityUseCase).createRoomAvailability(
                any(Room.class), any(LocalDate.class), any(LocalDate.class));

        // Verify that the room ID matches
        assertEquals(102L, roomCaptor.getValue().getId());

        // Verify that the date range is correct (today to today + 365 days)
        LocalDate today = LocalDate.now();
        LocalDate capturedStartDate = startDateCaptor.getValue();
        LocalDate capturedEndDate = endDateCaptor.getValue();

        assertEquals(today, capturedStartDate);
        assertEquals(today.plusDays(365), capturedEndDate);
    }

    @Test
    void createRoomsForUnit_whenRoomPortThrowsException_shouldPropagateException() {
        // Arrange
        when(getRoomUseCase.getRoomsByUnitId(unit.getId())).thenReturn(existingRooms);
        when(roomPort.saveAll(anyList())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(
                RuntimeException.class,
                () -> createRoomUseCase.createRoomsForUnit(unit, 2)
        );

        verify(getRoomUseCase).getRoomsByUnitId(unit.getId());
        verifyNoInteractions(createRoomAvailabilityUseCase);
    }

    @Test
    void createRoomsForUnit_whenCreateRoomAvailabilityThrowsException_shouldPropagateException() {        // Arrange
        when(getRoomUseCase.getRoomsByUnitId(unit.getId())).thenReturn(existingRooms);
        when(roomPort.saveAll(anyList())).thenReturn(savedRooms);
        doThrow(new RuntimeException("Availability creation error"))
                .when(createRoomAvailabilityUseCase)
                .createRoomAvailability(any(Room.class), any(LocalDate.class), any(LocalDate.class));

        // Act & Assert
        assertThrows(
                RuntimeException.class,
                () -> createRoomUseCase.createRoomsForUnit(unit, 2)
        );

        verify(getRoomUseCase).getRoomsByUnitId(unit.getId());
        verify(roomPort).saveAll(anyList());
    }
}