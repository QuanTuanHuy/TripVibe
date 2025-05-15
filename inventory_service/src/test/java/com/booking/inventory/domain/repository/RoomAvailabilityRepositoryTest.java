package com.booking.inventory.domain.repository;

import com.booking.inventory.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RoomAvailabilityRepositoryTest {

    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Test
    void shouldEnforceUniqueConstraintOnRoomIdAndDate() {
        // Arrange
        Property property = createProperty();
        RoomType roomType = createRoomType();
        Room room = createRoom(roomType, property);
        
        LocalDate today = LocalDate.now();
        
        // Create first availability
        RoomAvailability availability1 = RoomAvailability.builder()
                .room(room)
                .date(today)
                .status(RoomStatus.AVAILABLE)
                .price(new BigDecimal("100.00"))
                .basePrice(new BigDecimal("100.00"))
                .lastModified(LocalDateTime.now())
                .build();
        
        roomAvailabilityRepository.save(availability1);
        
        // Create second availability with same room and date
        RoomAvailability availability2 = RoomAvailability.builder()
                .room(room)
                .date(today)
                .status(RoomStatus.AVAILABLE)
                .price(new BigDecimal("120.00"))
                .basePrice(new BigDecimal("100.00"))
                .lastModified(LocalDateTime.now())
                .build();
        
        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            roomAvailabilityRepository.save(availability2);
            roomAvailabilityRepository.flush();
        });
    }

    @Test
    void shouldFindByBookingId() {
        // Arrange
        Property property = createProperty();
        RoomType roomType = createRoomType();
        Room room = createRoom(roomType, property);
        
        LocalDate today = LocalDate.now();
        String bookingId = "test-booking-123";
        
        RoomAvailability availability1 = RoomAvailability.builder()
                .room(room)
                .date(today)
                .status(RoomStatus.BOOKED)
                .bookingId(bookingId)
                .price(new BigDecimal("100.00"))
                .basePrice(new BigDecimal("100.00"))
                .lastModified(LocalDateTime.now())
                .build();
        
        RoomAvailability availability2 = RoomAvailability.builder()
                .room(room)
                .date(today.plusDays(1))
                .status(RoomStatus.BOOKED)
                .bookingId(bookingId)
                .price(new BigDecimal("100.00"))
                .basePrice(new BigDecimal("100.00"))
                .lastModified(LocalDateTime.now())
                .build();
        
        roomAvailabilityRepository.save(availability1);
        roomAvailabilityRepository.save(availability2);
        
        // Act
        var result = roomAvailabilityRepository.findByBookingId(bookingId);
        
        // Assert
        assertEquals(2, result.size());
        for (var availability : result) {
            assertEquals(bookingId, availability.getBookingId());
        }
    }

    @Test
    void shouldFindRoomsNeedingCleaning() {
        // Arrange
        Property property = createProperty();
        RoomType roomType = createRoomType();
        Room room = createRoom(roomType, property);
        
        LocalDate today = LocalDate.now();
        
        RoomAvailability availability1 = RoomAvailability.builder()
                .room(room)
                .date(today)
                .status(RoomStatus.CLEANING)
                .needsCleaning(true)
                .price(new BigDecimal("100.00"))
                .basePrice(new BigDecimal("100.00"))
                .lastModified(LocalDateTime.now())
                .build();
        
        RoomAvailability availability2 = RoomAvailability.builder()
                .room(room)
                .date(today.plusDays(1))
                .status(RoomStatus.AVAILABLE)
                .needsCleaning(false)
                .price(new BigDecimal("100.00"))
                .basePrice(new BigDecimal("100.00"))
                .lastModified(LocalDateTime.now())
                .build();
        
        roomAvailabilityRepository.save(availability1);
        roomAvailabilityRepository.save(availability2);
        
        // Act
        var result = roomAvailabilityRepository.findRoomsNeedingCleaning();
        
        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).isNeedsCleaning());
    }

    @Test
    void shouldMarkRoomAsClean() {
        // Arrange
        Property property = createProperty();
        RoomType roomType = createRoomType();
        Room room = createRoom(roomType, property);
        
        LocalDate today = LocalDate.now();
        
        RoomAvailability availability1 = RoomAvailability.builder()
                .room(room)
                .date(today)
                .status(RoomStatus.CLEANING)
                .needsCleaning(true)
                .price(new BigDecimal("100.00"))
                .basePrice(new BigDecimal("100.00"))
                .lastModified(LocalDateTime.now())
                .build();
        
        RoomAvailability availability2 = RoomAvailability.builder()
                .room(room)
                .date(today.plusDays(1))
                .status(RoomStatus.CLEANING)
                .needsCleaning(true)
                .price(new BigDecimal("100.00"))
                .basePrice(new BigDecimal("100.00"))
                .lastModified(LocalDateTime.now())
                .build();
        
        roomAvailabilityRepository.save(availability1);
        roomAvailabilityRepository.save(availability2);
        
        // Act
        int updatedCount = roomAvailabilityRepository.markRoomAsClean(room.getId());
        
        // Refresh from database
        var result = roomAvailabilityRepository.findByRoomId(room.getId());
        
        // Assert
        assertEquals(2, updatedCount);
        assertEquals(2, result.size());
        
        for (var availability : result) {
            assertEquals(RoomStatus.AVAILABLE, availability.getStatus());
            assertFalse(availability.isNeedsCleaning());
        }
    }

    @Test
    void shouldOptimisticLockingWork() {
        // Arrange
        Property property = createProperty();
        RoomType roomType = createRoomType();
        Room room = createRoom(roomType, property);
        
        LocalDate today = LocalDate.now();
        
        RoomAvailability availability = RoomAvailability.builder()
                .room(room)
                .date(today)
                .status(RoomStatus.AVAILABLE)
                .price(new BigDecimal("100.00"))
                .basePrice(new BigDecimal("100.00"))
                .lastModified(LocalDateTime.now())
                .version(0)
                .build();
        
        // Save initial version
        var savedAvailability = roomAvailabilityRepository.save(availability);
        
        // Update and increment version
        savedAvailability.setPrice(new BigDecimal("110.00"));
        roomAvailabilityRepository.save(savedAvailability);
        
        // Assert version was incremented
        var updatedAvailability = roomAvailabilityRepository.findById(savedAvailability.getId()).orElseThrow();
        assertEquals(1, updatedAvailability.getVersion());
        assertEquals(new BigDecimal("110.00"), updatedAvailability.getPrice());
    }
    
    // Helper methods to create test entities
    private Property createProperty() {
        Property property = new Property();
        property.setName("Test Property");
        property.setDescription("Test Property Description");
        property.setCity("Test City");
        
        return propertyRepository.save(property);
    }
    
    private RoomType createRoomType() {
        RoomType roomType = new RoomType();
        roomType.setName("Test Room Type");
        roomType.setDescription("Test Room Type Description");
        roomType.setMaxOccupancy(2);
        roomType.setBasePrice(new BigDecimal("100.00"));
        
        return roomTypeRepository.save(roomType);
    }
    
    private Room createRoom(RoomType roomType, Property property) {
        Room room = new Room();
        room.setRoomNumber("T101");
        room.setName("Test Room");
        room.setDescription("Test Room Description");
        room.setStatus(RoomStatus.AVAILABLE);
        room.setRoomType(roomType);
        room.setProperty(property);
        
        return roomRepository.save(room);
    }
}
