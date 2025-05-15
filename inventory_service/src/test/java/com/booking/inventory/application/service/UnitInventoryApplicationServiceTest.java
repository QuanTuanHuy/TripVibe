package com.booking.inventory.application.service;

import com.booking.inventory.application.dto.UnitAvailabilityRequest;
import com.booking.inventory.application.dto.UnitInventoryDto;
import com.booking.inventory.application.dto.UnitInventorySyncRequest;
import com.booking.inventory.domain.model.Room;
import com.booking.inventory.domain.model.UnitInventory;
import com.booking.inventory.domain.service.UnitInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitInventoryApplicationServiceTest {

    @Mock
    private UnitInventoryService unitInventoryService;
    
    @InjectMocks
    private UnitInventoryApplicationService applicationService;
    
    private UnitInventory unitInventory;
    private Room room1, room2;
    
    @BeforeEach
    void setUp() {
        room1 = Room.builder()
                .id(1L)
                .roomNumber("U201-1")
                .build();
                
        room2 = Room.builder()
                .id(2L)
                .roomNumber("U201-2")
                .build();
                
        List<Room> rooms = new ArrayList<>(Arrays.asList(room1, room2));
        
        unitInventory = UnitInventory.builder()
                .id(1L)
                .accommodationId(101L)
                .unitId(201L)
                .unitName("Deluxe Room")
                .quantity(2)
                .rooms(rooms)
                .build();
                
        // Set up bi-directional relationship
        room1.setUnitInventory(unitInventory);
        room2.setUnitInventory(unitInventory);
    }
    
    @Test
    void syncUnitInventory_ShouldReturnCorrectDto() {
        // Arrange
        UnitInventorySyncRequest request = UnitInventorySyncRequest.builder()
                .accommodationId(101L)
                .unitId(201L)
                .unitName("Deluxe Room")
                .quantity(2)
                .roomTypeId(301L)
                .build();
                
        when(unitInventoryService.syncUnitInventory(
                request.getAccommodationId(),
                request.getUnitId(),
                request.getUnitName(),
                request.getQuantity(),
                request.getRoomTypeId()
        )).thenReturn(unitInventory);
        
        // Act
        UnitInventoryDto result = applicationService.syncUnitInventory(request);
        
        // Assert
        assertNotNull(result);
        assertEquals(unitInventory.getId(), result.getId());
        assertEquals(unitInventory.getAccommodationId(), result.getAccommodationId());
        assertEquals(unitInventory.getUnitId(), result.getUnitId());
        assertEquals(unitInventory.getUnitName(), result.getUnitName());
        assertEquals(unitInventory.getQuantity(), result.getQuantity());
        assertEquals(2, result.getRoomNumbers().size());
        assertTrue(result.getRoomNumbers().contains("U201-1"));
        assertTrue(result.getRoomNumbers().contains("U201-2"));
    }
    
    @Test
    void findAllByAccommodationId_ShouldReturnListOfDtos() {
        // Arrange
        when(unitInventoryService.findAllByAccommodationId(101L))
                .thenReturn(Arrays.asList(unitInventory));
        
        // Act
        List<UnitInventoryDto> result = applicationService.findAllByAccommodationId(101L);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(unitInventory.getId(), result.get(0).getId());
        assertEquals(unitInventory.getUnitId(), result.get(0).getUnitId());
    }
    
    @Test
    void findByUnitId_WhenExists_ShouldReturnDto() {
        // Arrange
        when(unitInventoryService.findByUnitId(201L))
                .thenReturn(Optional.of(unitInventory));
        
        // Act
        Optional<UnitInventoryDto> result = applicationService.findByUnitId(201L);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(unitInventory.getId(), result.get().getId());
        assertEquals(unitInventory.getUnitName(), result.get().getUnitName());
    }
    
    @Test
    void findByUnitId_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(unitInventoryService.findByUnitId(999L))
                .thenReturn(Optional.empty());
        
        // Act
        Optional<UnitInventoryDto> result = applicationService.findByUnitId(999L);
        
        // Assert
        assertFalse(result.isPresent());
    }
    
    @Test
    void checkAvailability_WhenUnitExists_ShouldReturnDtoWithAvailableRooms() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(3);
        
        UnitAvailabilityRequest request = UnitAvailabilityRequest.builder()
                .unitId(201L)
                .startDate(startDate)
                .endDate(endDate)
                .build();
                
        when(unitInventoryService.findByUnitId(201L))
                .thenReturn(Optional.of(unitInventory));
                
        when(unitInventoryService.countAvailableRoomsByUnitId(
                request.getUnitId(),
                request.getStartDate(),
                request.getEndDate()
        )).thenReturn(1);
        
        // Act
        UnitInventoryDto result = applicationService.checkAvailability(request);
        
        // Assert
        assertNotNull(result);
        assertEquals(unitInventory.getId(), result.getId());
        assertEquals(1, result.getAvailableRooms());
    }
    
    @Test
    void checkAvailability_WhenUnitDoesNotExist_ShouldReturnNull() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(3);
        
        UnitAvailabilityRequest request = UnitAvailabilityRequest.builder()
                .unitId(999L)
                .startDate(startDate)
                .endDate(endDate)
                .build();
                
        when(unitInventoryService.findByUnitId(999L))
                .thenReturn(Optional.empty());
        
        // Act
        UnitInventoryDto result = applicationService.checkAvailability(request);
        
        // Assert
        assertNull(result);
    }
}
