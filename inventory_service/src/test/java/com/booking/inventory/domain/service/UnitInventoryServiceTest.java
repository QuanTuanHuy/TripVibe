package com.booking.inventory.domain.service;

import com.booking.inventory.domain.model.Property;
import com.booking.inventory.domain.model.Room;
import com.booking.inventory.domain.model.RoomType;
import com.booking.inventory.domain.model.UnitInventory;
import com.booking.inventory.domain.repository.PropertyRepository;
import com.booking.inventory.domain.repository.RoomRepository;
import com.booking.inventory.domain.repository.RoomTypeRepository;
import com.booking.inventory.domain.repository.UnitInventoryRepository;
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
class UnitInventoryServiceTest {

    @Mock
    private UnitInventoryRepository unitInventoryRepository;
    
    @Mock
    private PropertyRepository propertyRepository;
    
    @Mock
    private RoomTypeRepository roomTypeRepository;
    
    @Mock
    private RoomRepository roomRepository;
    
    @Mock
    private RoomAvailabilityService roomAvailabilityService;
    
    @InjectMocks
    private UnitInventoryService unitInventoryService;
    
    private Property property;
    private RoomType roomType;
    private UnitInventory unitInventory;
    private Room room;
    
    @BeforeEach
    void setUp() {
        property = Property.builder()
                .id(1L)
                .name("Test Hotel")
                .externalId(101L)
                .build();
        
        roomType = RoomType.builder()
                .id(1L)
                .name("Deluxe Room")
                .externalId(201L)
                .build();
        
        unitInventory = UnitInventory.builder()
                .id(1L)
                .accommodationId(101L)
                .unitId(201L)
                .unitName("Deluxe Room")
                .quantity(3)
                .rooms(new ArrayList<>())
                .build();
        
        room = Room.builder()
                .id(1L)
                .roomNumber("U201-1")
                .name("Deluxe Room - U201-1")
                .property(property)
                .roomType(roomType)
                .unitInventory(unitInventory)
                .build();
    }
    
    @Test
    void syncUnitInventory_WhenUnitDoesNotExist_ShouldCreateNew() {
        // Arrange
        when(unitInventoryRepository.findByUnitId(201L)).thenReturn(Optional.empty());
        when(propertyRepository.findByExternalId(101L)).thenReturn(Optional.of(property));
        when(roomTypeRepository.findByExternalId(201L)).thenReturn(Optional.of(roomType));
        when(unitInventoryRepository.save(any(UnitInventory.class))).thenReturn(unitInventory);
        
        // Act
        UnitInventory result = unitInventoryService.syncUnitInventory(101L, 201L, "Deluxe Room", 3, 201L);
        
        // Assert
        assertNotNull(result);
        assertEquals(101L, result.getAccommodationId());
        assertEquals(201L, result.getUnitId());
        assertEquals("Deluxe Room", result.getUnitName());
        assertEquals(3, result.getQuantity());
        
        verify(unitInventoryRepository).save(any(UnitInventory.class));
        verify(roomRepository).saveAll(anyList());
    }
    
    @Test
    void syncUnitInventory_WhenUnitExists_ShouldUpdate() {
        // Arrange
        when(unitInventoryRepository.findByUnitId(201L)).thenReturn(Optional.of(unitInventory));
        when(propertyRepository.findByExternalId(101L)).thenReturn(Optional.of(property));
        when(roomTypeRepository.findByExternalId(201L)).thenReturn(Optional.of(roomType));
        when(unitInventoryRepository.save(any(UnitInventory.class))).thenReturn(unitInventory);
        when(roomRepository.findByUnitInventoryId(1L)).thenReturn(Arrays.asList(room));
        
        // Act
        UnitInventory result = unitInventoryService.syncUnitInventory(101L, 201L, "Deluxe Room Updated", 4, 201L);
        
        // Assert
        assertNotNull(result);
        assertEquals("Deluxe Room Updated", result.getUnitName());
        assertEquals(4, result.getQuantity());
        
        verify(unitInventoryRepository).save(any(UnitInventory.class));
        verify(roomRepository).saveAll(anyList());
    }
    
    @Test
    void getAvailableRoomsByUnitId_ShouldReturnAvailableRooms() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        List<Room> roomList = Arrays.asList(room);
        
        when(unitInventoryRepository.findByUnitId(201L)).thenReturn(Optional.of(unitInventory));
        when(roomRepository.findByUnitInventoryId(1L)).thenReturn(roomList);
        when(roomAvailabilityService.isRoomAvailable(1L, startDate, endDate)).thenReturn(true);
        
        // Act
        List<Room> result = unitInventoryService.getAvailableRoomsByUnitId(201L, startDate, endDate);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(room.getId(), result.get(0).getId());
    }
    
    @Test
    void countAvailableRoomsByUnitId_ShouldReturnCorrectCount() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        List<Room> roomList = Arrays.asList(room);
        
        when(unitInventoryRepository.findByUnitId(201L)).thenReturn(Optional.of(unitInventory));
        when(roomRepository.findByUnitInventoryId(1L)).thenReturn(roomList);
        when(roomAvailabilityService.isRoomAvailable(1L, startDate, endDate)).thenReturn(true);
        
        // Act
        int count = unitInventoryService.countAvailableRoomsByUnitId(201L, startDate, endDate);
        
        // Assert
        assertEquals(1, count);
    }
    
    @Test
    void isUnitAvailable_WhenRoomsAvailable_ShouldReturnTrue() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        List<Room> roomList = Arrays.asList(room);
        
        when(unitInventoryRepository.findByUnitId(201L)).thenReturn(Optional.of(unitInventory));
        when(roomRepository.findByUnitInventoryId(1L)).thenReturn(roomList);
        when(roomAvailabilityService.isRoomAvailable(1L, startDate, endDate)).thenReturn(true);
        
        // Act
        boolean result = unitInventoryService.isUnitAvailable(201L, startDate, endDate);
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    void isUnitAvailable_WhenNoRoomsAvailable_ShouldReturnFalse() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        List<Room> roomList = Arrays.asList(room);
        
        when(unitInventoryRepository.findByUnitId(201L)).thenReturn(Optional.of(unitInventory));
        when(roomRepository.findByUnitInventoryId(1L)).thenReturn(roomList);
        when(roomAvailabilityService.isRoomAvailable(1L, startDate, endDate)).thenReturn(false);
        
        // Act
        boolean result = unitInventoryService.isUnitAvailable(201L, startDate, endDate);
        
        // Assert
        assertFalse(result);
    }
}
