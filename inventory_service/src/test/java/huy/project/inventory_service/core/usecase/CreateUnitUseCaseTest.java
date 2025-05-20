package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.dto.request.SyncUnitDto;
import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.port.IUnitPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUnitUseCaseTest {

    @Mock
    private IUnitPort unitPort;

    @Mock
    private CreateRoomUseCase createRoomUseCase;

    @InjectMocks
    private CreateUnitUseCase createUnitUseCase;

    @Captor
    private ArgumentCaptor<Unit> unitCaptor;

    private SyncUnitDto request;
    private Unit savedUnit;

    @BeforeEach
    void setUp() {
        // Setup test data
        request = new SyncUnitDto();
        request.setUnitId(1L);
        request.setAccommodationId(100L);
        request.setUnitNameId(10L);
        request.setUnitName("Deluxe Room");
        request.setBasePrice(new BigDecimal("200.00"));
        request.setQuantity(3);

        savedUnit = Unit.builder()
                .id(1L)
                .accommodationId(100L)
                .unitNameId(10L)
                .unitName("Deluxe Room")
                .basePrice(new BigDecimal("200.00"))
                .quantity(3)
                .build();
    }

    @Test
    void createUnit_shouldSaveUnitAndCreateRooms() {
        // Arrange
        when(unitPort.save(any(Unit.class))).thenReturn(savedUnit);
        doNothing().when(createRoomUseCase).createRoomsForUnit(any(Unit.class), anyInt());

        // Act
        Unit result = createUnitUseCase.createUnit(request);

        // Assert
        assertNotNull(result);
        assertEquals(request.getUnitId(), result.getId());
        assertEquals(request.getAccommodationId(), result.getAccommodationId());
        assertEquals(request.getUnitNameId(), result.getUnitNameId());
        assertEquals(request.getUnitName(), result.getUnitName());
        assertEquals(request.getBasePrice(), result.getBasePrice());
        assertEquals(request.getQuantity(), result.getQuantity());

        // Verify that unitPort.save() was called with correct Unit object
        verify(unitPort).save(unitCaptor.capture());
        Unit capturedUnit = unitCaptor.getValue();
        assertEquals(request.getUnitId(), capturedUnit.getId());
        assertEquals(request.getAccommodationId(), capturedUnit.getAccommodationId());
        assertEquals(request.getUnitNameId(), capturedUnit.getUnitNameId());
        assertEquals(request.getUnitName(), capturedUnit.getUnitName());
        assertEquals(request.getBasePrice(), capturedUnit.getBasePrice());
        assertEquals(request.getQuantity(), capturedUnit.getQuantity());

        // Verify that createRoomUseCase.createRoomsForUnit() was called with correct arguments
        verify(createRoomUseCase).createRoomsForUnit(savedUnit, request.getQuantity());
    }

    @Test
    void createUnit_withZeroQuantity_shouldNotCreateRooms() {
        // Arrange
        request.setQuantity(0);
        
        Unit savedUnitWithZeroQuantity = Unit.builder()
                .id(1L)
                .accommodationId(100L)
                .unitNameId(10L)
                .unitName("Deluxe Room")
                .basePrice(new BigDecimal("200.00"))
                .quantity(0)
                .build();
                
        when(unitPort.save(any(Unit.class))).thenReturn(savedUnitWithZeroQuantity);

        // Act
        Unit result = createUnitUseCase.createUnit(request);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getQuantity());
        
        // Verify that createRoomUseCase.createRoomsForUnit() was called with zero quantity
        verify(createRoomUseCase).createRoomsForUnit(savedUnitWithZeroQuantity, 0);
    }

    @Test
    void createUnit_whenUnitPortThrowsException_shouldPropagateException() {
        // Arrange
        when(unitPort.save(any(Unit.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> createUnitUseCase.createUnit(request));
        
        // Verify that createRoomUseCase.createRoomsForUnit() was not called
        verify(createRoomUseCase, never()).createRoomsForUnit(any(Unit.class), anyInt());
    }

    @Test
    void createUnit_whenCreateRoomUseCaseThrowsException_shouldPropagateException() {
        // Arrange
        when(unitPort.save(any(Unit.class))).thenReturn(savedUnit);
        doThrow(new RuntimeException("Error creating rooms")).when(createRoomUseCase).createRoomsForUnit(any(Unit.class), anyInt());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> createUnitUseCase.createUnit(request));
        
        // Verify that unitPort.save() was called
        verify(unitPort).save(any(Unit.class));
    }
}
