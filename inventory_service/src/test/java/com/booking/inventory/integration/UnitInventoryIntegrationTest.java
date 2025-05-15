package com.booking.inventory.integration;

import com.booking.inventory.domain.model.Property;
import com.booking.inventory.domain.model.Room;
import com.booking.inventory.domain.model.RoomType;
import com.booking.inventory.domain.model.UnitInventory;
import com.booking.inventory.domain.repository.PropertyRepository;
import com.booking.inventory.domain.repository.RoomRepository;
import com.booking.inventory.domain.repository.RoomTypeRepository;
import com.booking.inventory.domain.repository.UnitInventoryRepository;
import com.booking.inventory.domain.service.RoomAvailabilityService;
import com.booking.inventory.domain.service.UnitInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UnitInventoryIntegrationTest {

    @Autowired
    private UnitInventoryService unitInventoryService;
    
    @Autowired
    private UnitInventoryRepository unitInventoryRepository;
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomAvailabilityService roomAvailabilityService;
    
    private Property property;
    private RoomType roomType;
    private LocalDate today;
    
    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        
        property = Property.builder()
                .name("Test Hotel")
                .externalId(101L)
                .description("Test Hotel Description")
                .build();
        property = propertyRepository.save(property);
        
        roomType = RoomType.builder()
                .name("Deluxe Room")
                .externalId(201L)
                .description("Deluxe Room Description")
                .build();
        roomType = roomTypeRepository.save(roomType);
    }
    
    @Test
    void testUnitInventorySynchronization() {
        // 1. Create a new unit inventory
        UnitInventory unitInventory = unitInventoryService.syncUnitInventory(
                101L, 201L, "Deluxe Room", 3, 201L);
        
        // Verify unit was created
        assertNotNull(unitInventory);
        assertEquals(101L, unitInventory.getAccommodationId());
        assertEquals(201L, unitInventory.getUnitId());
        assertEquals("Deluxe Room", unitInventory.getUnitName());
        assertEquals(3, unitInventory.getQuantity());
        
        // Verify rooms were created
        List<Room> rooms = roomRepository.findByUnitInventoryId(unitInventory.getId());
        assertEquals(3, rooms.size());
        
        // 2. Update the unit inventory with increased quantity
        unitInventory = unitInventoryService.syncUnitInventory(
                101L, 201L, "Deluxe Room Updated", 5, 201L);
        
        // Verify unit was updated
        assertEquals("Deluxe Room Updated", unitInventory.getUnitName());
        assertEquals(5, unitInventory.getQuantity());
        
        // Verify rooms were added
        rooms = roomRepository.findByUnitInventoryId(unitInventory.getId());
        assertEquals(5, rooms.size());
        
        // 3. Update unit inventory with decreased quantity
        unitInventory = unitInventoryService.syncUnitInventory(
                101L, 201L, "Deluxe Room Updated", 2, 201L);
        
        // Verify rooms were removed
        rooms = roomRepository.findByUnitInventoryId(unitInventory.getId());
        assertEquals(2, rooms.size());
    }
    
    @Test
    void testUnitAvailabilityChecking() {
        // 1. Create a new unit inventory with 2 rooms
        UnitInventory unitInventory = unitInventoryService.syncUnitInventory(
                101L, 201L, "Deluxe Room", 2, 201L);
                
        // Get the created rooms
        List<Room> rooms = roomRepository.findByUnitInventoryId(unitInventory.getId());
        assertEquals(2, rooms.size());
        
        // 2. Check if unit is available (should be available)
        LocalDate checkInDate = today.plusDays(1);
        LocalDate checkOutDate = today.plusDays(3);
        
        boolean isAvailable = unitInventoryService.isUnitAvailable(
                201L, checkInDate, checkOutDate);
        assertTrue(isAvailable);
        
        // 3. Count available rooms
        int availableCount = unitInventoryService.countAvailableRoomsByUnitId(
                201L, checkInDate, checkOutDate);
        assertEquals(2, availableCount);
        
        // 4. Get available rooms
        List<Room> availableRooms = unitInventoryService.getAvailableRoomsByUnitId(
                201L, checkInDate, checkOutDate);
        assertEquals(2, availableRooms.size());
    }
    
    @Test
    void testFindingUnitInventories() {
        // 1. Create multiple unit inventories
        unitInventoryService.syncUnitInventory(101L, 201L, "Deluxe Room", 2, 201L);
        unitInventoryService.syncUnitInventory(101L, 202L, "Standard Room", 3, 202L);
        unitInventoryService.syncUnitInventory(102L, 203L, "Suite", 1, 203L);
        
        // 2. Find all by accommodation ID
        List<UnitInventory> units = unitInventoryService.findAllByAccommodationId(101L);
        assertEquals(2, units.size());
        
        // 3. Find by unit ID
        Optional<UnitInventory> unitOpt = unitInventoryService.findByUnitId(202L);
        assertTrue(unitOpt.isPresent());
        assertEquals("Standard Room", unitOpt.get().getUnitName());
        assertEquals(3, unitOpt.get().getQuantity());
    }
}
