package huy.project.inventory_service.core.service.impl;

import huy.project.inventory_service.core.domain.dto.request.SyncUnitDto;
import huy.project.inventory_service.core.domain.entity.Accommodation;
import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.domain.exception.NotFoundException;
import huy.project.inventory_service.core.usecase.CreateUnitUseCase;
import huy.project.inventory_service.core.usecase.GetAccommodationUseCase;
import huy.project.inventory_service.core.usecase.GetUnitUseCase;
import huy.project.inventory_service.core.usecase.UpdateUnitUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private CreateUnitUseCase createUnitUseCase;

    @Mock
    private GetUnitUseCase getUnitUseCase;

    @Mock
    private UpdateUnitUseCase updateUnitUseCase;

    @Mock
    private GetAccommodationUseCase getAccommodationUseCase;

    @InjectMocks
    private UnitService unitService;

    private SyncUnitDto syncUnitDto;
    private Unit unit;
    private Accommodation accommodation;

    @BeforeEach
    void setUp() {
        syncUnitDto = new SyncUnitDto();
        syncUnitDto.setAccommodationId(1L);
        syncUnitDto.setUnitId(2L);
        syncUnitDto.setUnitNameId(3L);
        syncUnitDto.setUnitName("Deluxe Room");
        syncUnitDto.setBasePrice(new BigDecimal("150.00"));
        syncUnitDto.setQuantity(5);

        unit = Unit.builder()
                .id(2L)
                .accommodationId(1L)
                .unitNameId(3L)
                .unitName("Deluxe Room")
                .basePrice(new BigDecimal("150.00"))
                .quantity(5)
                .build();

        accommodation = Accommodation.builder()
                .id(1L)
                .name("Sample Hotel")
                .build();
    }

    @Test
    void syncUnit_whenUnitExists_shouldUpdateUnit() {
        // Arrange
        when(getAccommodationUseCase.getAccommodationById(syncUnitDto.getAccommodationId())).thenReturn(accommodation);
        when(getUnitUseCase.getUnitById(syncUnitDto.getUnitId())).thenReturn(unit);
        when(updateUnitUseCase.updateUnit(syncUnitDto)).thenReturn(unit);

        // Act
        Unit result = unitService.syncUnit(syncUnitDto);

        // Assert
        assertNotNull(result);
        assertEquals(unit.getId(), result.getId());
        assertEquals(unit.getAccommodationId(), result.getAccommodationId());
        assertEquals(unit.getUnitName(), result.getUnitName());
        assertEquals(unit.getQuantity(), result.getQuantity());
        
        verify(getAccommodationUseCase).getAccommodationById(syncUnitDto.getAccommodationId());
        verify(getUnitUseCase).getUnitById(syncUnitDto.getUnitId());
        verify(updateUnitUseCase).updateUnit(syncUnitDto);
        verify(createUnitUseCase, never()).createUnit(any());
    }

    @Test
    void syncUnit_whenUnitDoesNotExist_shouldCreateUnit() {
        // Arrange
        when(getAccommodationUseCase.getAccommodationById(syncUnitDto.getAccommodationId())).thenReturn(accommodation);
        when(getUnitUseCase.getUnitById(syncUnitDto.getUnitId())).thenReturn(null);
        when(createUnitUseCase.createUnit(syncUnitDto)).thenReturn(unit);

        // Act
        Unit result = unitService.syncUnit(syncUnitDto);

        // Assert
        assertNotNull(result);
        assertEquals(unit.getId(), result.getId());
        assertEquals(unit.getAccommodationId(), result.getAccommodationId());
        assertEquals(unit.getUnitName(), result.getUnitName());
        assertEquals(unit.getQuantity(), result.getQuantity());

        verify(getAccommodationUseCase).getAccommodationById(syncUnitDto.getAccommodationId());
        verify(getUnitUseCase).getUnitById(syncUnitDto.getUnitId());
        verify(createUnitUseCase).createUnit(syncUnitDto);
        verify(updateUnitUseCase, never()).updateUnit(any());
    }

    @Test
    void syncUnit_whenAccommodationNotFound_shouldThrowNotFoundException() {
        // Arrange
        when(getAccommodationUseCase.getAccommodationById(syncUnitDto.getAccommodationId()))
                .thenThrow(new NotFoundException("Accommodation not found, id: " + syncUnitDto.getAccommodationId()));

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> unitService.syncUnit(syncUnitDto));

        assertTrue(exception.getMessage().contains("Accommodation not found"));
        verify(getAccommodationUseCase).getAccommodationById(syncUnitDto.getAccommodationId());
        verify(getUnitUseCase, never()).getUnitById(any());
        verify(createUnitUseCase, never()).createUnit(any());
        verify(updateUnitUseCase, never()).updateUnit(any());
    }

    @Test
    void syncUnit_logsInformation() {
        // Arrange
        when(getAccommodationUseCase.getAccommodationById(syncUnitDto.getAccommodationId())).thenReturn(accommodation);
        when(getUnitUseCase.getUnitById(syncUnitDto.getUnitId())).thenReturn(unit);
        when(updateUnitUseCase.updateUnit(syncUnitDto)).thenReturn(unit);

        // Act
        unitService.syncUnit(syncUnitDto);

        // Assert
        // Since we can't easily verify log output in a unit test without additional libraries,
        // we're primarily verifying that the method executes without errors
        verify(getAccommodationUseCase).getAccommodationById(syncUnitDto.getAccommodationId());
        verify(getUnitUseCase).getUnitById(syncUnitDto.getUnitId());
        verify(updateUnitUseCase).updateUnit(syncUnitDto);
    }
}
