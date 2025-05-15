package com.booking.inventory.integration;

import com.booking.inventory.domain.model.Room;
import com.booking.inventory.domain.model.RoomAvailability;
import com.booking.inventory.domain.model.RoomStatus;
import com.booking.inventory.domain.model.RoomType;
import com.booking.inventory.domain.repository.RoomAvailabilityRepository;
import com.booking.inventory.domain.repository.RoomRepository;
import com.booking.inventory.domain.repository.RoomTypeRepository;
import com.booking.inventory.domain.service.RoomAvailabilityService;
import com.booking.inventory.infrastructure.redis.RoomLockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoomAvailabilityIntegrationTest {

    @Autowired
    private RoomAvailabilityService roomAvailabilityService;
    
    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    @MockBean
    private RoomLockRepository roomLockRepository; // Mock Redis for tests
    
    private Room room;
    private LocalDate today;
    private String sessionId;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        sessionId = UUID.randomUUID().toString();
        
        // Create room type
        RoomType roomType = new RoomType();
        roomType.setName("Test Room Type");
        roomType.setDescription("Test Room Type Description");
        roomType.setMaxOccupancy(2);
        roomType.setBasePrice(new BigDecimal("100.00"));
        roomTypeRepository.save(roomType);
        
        // Create room
        room = new Room();
        room.setRoomNumber("T101");
        room.setName("Test Room");
        room.setDescription("Test Room Description");
        room.setStatus(RoomStatus.AVAILABLE);
        room.setRoomType(roomType);
        roomRepository.save(room);
        
        // Initialize room availability for testing period
        LocalDate startDate = today;
        LocalDate endDate = today.plusDays(10);
        
        roomAvailabilityService.initializeRoomAvailability(room.getId(), startDate, endDate);
    }

    @Test
    void testRoomAvailabilityLifecycle() {
        Long roomId = room.getId();
        LocalDate checkInDate = today.plusDays(1);
        LocalDate checkOutDate = today.plusDays(3);
        String bookingId = UUID.randomUUID().toString();
        
        // 1. Verify room is available before operations
        assertTrue(roomAvailabilityService.isRoomAvailable(roomId, checkInDate, checkOutDate));
        
        // 2. Lock the room for booking
        boolean lockResult = roomAvailabilityService.lockRoom(roomId, checkInDate, checkOutDate, sessionId);
        assertTrue(lockResult, "Room should be locked successfully");
        
        // 3. Verify room is no longer available after locking
        assertFalse(roomAvailabilityService.isRoomAvailable(roomId, checkInDate, checkOutDate));
        
        // 4. Check database status after locking
        Optional<RoomAvailability> lockedAvailability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, checkInDate);
        assertTrue(lockedAvailability.isPresent());
        assertEquals(RoomStatus.TEMPORARILY_LOCKED, lockedAvailability.get().getStatus());
        
        // 5. Confirm booking
        boolean confirmationResult = roomAvailabilityService.confirmBooking(
            roomId, checkInDate, checkOutDate, sessionId, bookingId);
        assertTrue(confirmationResult, "Booking should be confirmed successfully");
        
        // 6. Verify booking status in database
        Optional<RoomAvailability> bookedAvailability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, checkInDate);
        assertTrue(bookedAvailability.isPresent());
        assertEquals(RoomStatus.BOOKED, bookedAvailability.get().getStatus());
        assertEquals(bookingId, bookedAvailability.get().getBookingId());
        
        // 7. Process check-in
        boolean checkInResult = roomAvailabilityService.processCheckIn(bookingId);
        assertTrue(checkInResult, "Check-in should be processed successfully");
        
        // 8. Verify room status after check-in
        Optional<RoomAvailability> checkedInAvailability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, checkInDate);
        assertTrue(checkedInAvailability.isPresent());
        assertEquals(RoomStatus.OCCUPIED, checkedInAvailability.get().getStatus());
        
        // 9. Process check-out
        boolean checkOutResult = roomAvailabilityService.processCheckOut(bookingId);
        assertTrue(checkOutResult, "Check-out should be processed successfully");
        
        // 10. Verify room needs cleaning after check-out
        Optional<RoomAvailability> checkedOutAvailability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, checkInDate);
        assertTrue(checkedOutAvailability.isPresent());
        assertEquals(RoomStatus.CLEANING, checkedOutAvailability.get().getStatus());
        assertTrue(checkedOutAvailability.get().isNeedsCleaning());
        
        // 11. Mark room as cleaned
        boolean cleaningResult = roomAvailabilityService.markRoomAsCleaned(roomId);
        assertTrue(cleaningResult, "Room should be marked as cleaned successfully");
        
        // 12. Verify room is available again after cleaning
        Optional<RoomAvailability> cleanedAvailability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, checkInDate);
        assertTrue(cleanedAvailability.isPresent());
        assertEquals(RoomStatus.AVAILABLE, cleanedAvailability.get().getStatus());
        assertFalse(cleanedAvailability.get().isNeedsCleaning());
    }
    
    @Test
    void testBookingCancellation() {
        Long roomId = room.getId();
        LocalDate checkInDate = today.plusDays(4);
        LocalDate checkOutDate = today.plusDays(6);
        String bookingId = UUID.randomUUID().toString();
        
        // 1. Lock and book the room
        roomAvailabilityService.lockRoom(roomId, checkInDate, checkOutDate, sessionId);
        roomAvailabilityService.confirmBooking(roomId, checkInDate, checkOutDate, sessionId, bookingId);
        
        // 2. Verify booking is confirmed
        Optional<RoomAvailability> bookedAvailability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, checkInDate);
        assertTrue(bookedAvailability.isPresent());
        assertEquals(RoomStatus.BOOKED, bookedAvailability.get().getStatus());
        
        // 3. Cancel booking
        boolean cancellationResult = roomAvailabilityService.cancelBooking(bookingId);
        assertTrue(cancellationResult, "Booking should be cancelled successfully");
        
        // 4. Verify room is available again after cancellation
        Optional<RoomAvailability> cancelledAvailability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, checkInDate);
        assertTrue(cancelledAvailability.isPresent());
        assertEquals(RoomStatus.AVAILABLE, cancelledAvailability.get().getStatus());
        assertNull(cancelledAvailability.get().getBookingId());
    }
    
    @Test
    void testOccupancyRateCalculation() {
        Long roomId = room.getId();
        LocalDate targetDate = today.plusDays(7);
        String bookingId1 = UUID.randomUUID().toString();
        String bookingId2 = UUID.randomUUID().toString();
        
        // Initial occupancy should be 0%
        double initialOccupancy = roomAvailabilityService.calculateOccupancyRate(1L, targetDate);
        assertEquals(0.0, initialOccupancy);
        
        // Book one room
        roomAvailabilityService.lockRoom(roomId, targetDate, targetDate, sessionId);
        roomAvailabilityService.confirmBooking(roomId, targetDate, targetDate, sessionId, bookingId1);
        
        // Create another room and book it too
        Room room2 = new Room();
        room2.setRoomNumber("T102");
        room2.setName("Test Room 2");
        room2.setDescription("Test Room Description 2");
        room2.setStatus(RoomStatus.AVAILABLE);
        room2.setRoomType(room.getRoomType());
        room2.setProperty(room.getProperty());
        roomRepository.save(room2);
        
        roomAvailabilityService.initializeRoomAvailability(room2.getId(), targetDate, targetDate);
        
        // With 1 out of 2 rooms booked, occupancy should be 50%
        double midOccupancy = roomAvailabilityService.calculateOccupancyRate(1L, targetDate);
        assertEquals(50.0, midOccupancy);
    }
    
    @Test
    void testMaintenanceScheduling() {
        Long roomId = room.getId();
        LocalDate maintenanceDate = today.plusDays(9);
        
        // Get initial availability
        Optional<RoomAvailability> initialAvailability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, maintenanceDate);
        assertTrue(initialAvailability.isPresent());
        assertFalse(initialAvailability.get().isNeedsMaintenance());
        
        // Schedule maintenance
        RoomAvailability availability = initialAvailability.get();
        availability.setNeedsMaintenance(true);
        availability.setMaintenanceNotes("Annual HVAC maintenance");
        availability.setStatus(RoomStatus.MAINTENANCE);
        roomAvailabilityRepository.save(availability);
        
        // Verify maintenance is scheduled
        Optional<RoomAvailability> maintenanceAvailability = roomAvailabilityRepository.findByRoomIdAndDate(roomId, maintenanceDate);
        assertTrue(maintenanceAvailability.isPresent());
        assertTrue(maintenanceAvailability.get().isNeedsMaintenance());
        assertEquals(RoomStatus.MAINTENANCE, maintenanceAvailability.get().getStatus());
        assertEquals("Annual HVAC maintenance", maintenanceAvailability.get().getMaintenanceNotes());
        
        // Verify room is not available during maintenance
        assertFalse(roomAvailabilityService.isRoomAvailable(roomId, maintenanceDate, maintenanceDate));
    }
}
