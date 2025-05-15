package com.booking.inventory.domain.service;

import com.booking.inventory.domain.model.*;
import com.booking.inventory.domain.repository.RoomAvailabilityRepository;
import com.booking.inventory.domain.repository.RoomRepository;
import com.booking.inventory.infrastructure.redis.RoomLockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomLockServiceTest {

    @Mock
    private RoomLockRepository roomLockRepository;

    @Mock
    private RoomAvailabilityRepository roomAvailabilityRepository;

    @InjectMocks
    private RoomAvailabilityService roomAvailabilityService;

    @Test
    void confirmBooking_WhenRoomIsLocked_ReturnsTrue() {
        // Arrange
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        String sessionId = "test-session";
        String bookingId = "test-booking";
        
        // Create test room
        Room room = Room.builder()
                .id(roomId)
                .roomNumber("101")
                .status(RoomStatus.AVAILABLE)
                .build();
        
        // Create test availability
        RoomAvailability availability = RoomAvailability.builder()
                .id(1L)
                .room(room)
                .date(startDate)
                .status(RoomStatus.TEMPORARILY_LOCKED)
                .lockId("test-lock")
                .build();
        
        // Create test lock
        RoomLock lock = RoomLock.builder()
                .id("test-lock")
                .roomId(roomId)
                .date(startDate)
                .sessionId(sessionId)
                .build();
        
        // Mock repository calls
        List<RoomLock> locks = new ArrayList<>();
        locks.add(lock);
        
        when(roomLockRepository.findBySessionId(sessionId)).thenReturn(locks);
        when(roomLockRepository.findByRoomIdAndDate(anyLong(), any(LocalDate.class))).thenReturn(Optional.of(lock));
        when(roomAvailabilityRepository.findByRoomIdAndDate(anyLong(), any(LocalDate.class))).thenReturn(Optional.of(availability));
        when(roomAvailabilityRepository.saveAll(anyList())).thenReturn(Collections.singletonList(availability));
        doNothing().when(roomLockRepository).deleteBySessionId(anyString());
        
        // Act
        boolean result = roomAvailabilityService.confirmBooking(roomId, startDate, endDate, sessionId, bookingId);
        
        // Assert
        assertTrue(result);
        
        // Verify booking was confirmed
        ArgumentCaptor<List<RoomAvailability>> captor = ArgumentCaptor.forClass(List.class);
        verify(roomAvailabilityRepository).saveAll(captor.capture());
        
        List<RoomAvailability> savedAvailabilities = captor.getValue();
        assertFalse(savedAvailabilities.isEmpty());
        assertEquals(RoomStatus.BOOKED, savedAvailabilities.get(0).getStatus());
        assertEquals(bookingId, savedAvailabilities.get(0).getBookingId());
        
        // Verify locks were deleted
        verify(roomLockRepository).deleteBySessionId(sessionId);
    }
    
    @Test
    void confirmBooking_WhenRoomIsNotLocked_ReturnsFalse() {
        // Arrange
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        String sessionId = "test-session";
        String bookingId = "test-booking";
        
        // Mock empty locks
        when(roomLockRepository.findBySessionId(sessionId)).thenReturn(Collections.emptyList());
        
        // Act
        boolean result = roomAvailabilityService.confirmBooking(roomId, startDate, endDate, sessionId, bookingId);
        
        // Assert
        assertFalse(result);
        verify(roomLockRepository, never()).deleteBySessionId(any());
        verify(roomAvailabilityRepository, never()).saveAll(anyList());
    }
    
    @Test
    void releaseExpiredLocks_ShouldReleaseAndCleanup() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        
        Room room = Room.builder()
                .id(1L)
                .roomNumber("101")
                .status(RoomStatus.AVAILABLE)
                .build();
        
        // Create expired locks
        RoomAvailability expiredLock1 = RoomAvailability.builder()
                .id(1L)
                .room(room)
                .date(today)
                .status(RoomStatus.TEMPORARILY_LOCKED)
                .lockId("lock-1")
                .lockExpirationDate(yesterday)
                .build();
        
        RoomAvailability expiredLock2 = RoomAvailability.builder()
                .id(2L)
                .room(room)
                .date(today.plusDays(1))
                .status(RoomStatus.TEMPORARILY_LOCKED)
                .lockId("lock-2")
                .lockExpirationDate(yesterday)
                .build();
        
        List<RoomAvailability> expiredLocks = Arrays.asList(expiredLock1, expiredLock2);
        
        when(roomAvailabilityRepository.findByStatusAndLockExpirationDateBefore(
                eq(RoomStatus.TEMPORARILY_LOCKED), any(LocalDate.class))).thenReturn(expiredLocks);
        when(roomAvailabilityRepository.saveAll(anyList())).thenReturn(expiredLocks);
        
        // Act
        roomAvailabilityService.releaseExpiredLocks();
        
        // Assert
        ArgumentCaptor<List<RoomAvailability>> captor = ArgumentCaptor.forClass(List.class);
        verify(roomAvailabilityRepository).saveAll(captor.capture());
        
        List<RoomAvailability> savedAvailabilities = captor.getValue();
        assertEquals(2, savedAvailabilities.size());
        
        // Verify all locks were released
        for (RoomAvailability availability : savedAvailabilities) {
            assertEquals(RoomStatus.AVAILABLE, availability.getStatus());
            assertNull(availability.getLockId());
            assertNull(availability.getLockExpirationDate());
        }
    }
    
    @Test
    void processCheckIn_ShouldUpdateStatusAndScheduleHousekeeping() {
        // Arrange
        String bookingId = "test-booking";
        LocalDate today = LocalDate.now();
        LocalDate checkoutDate = today.plusDays(3);
        
        Room room = Room.builder()
                .id(1L)
                .roomNumber("101")
                .status(RoomStatus.AVAILABLE)
                .build();
        
        List<RoomAvailability> bookedAvailabilities = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            bookedAvailabilities.add(RoomAvailability.builder()
                    .id((long) (i + 1))
                    .room(room)
                    .date(today.plusDays(i))
                    .status(RoomStatus.BOOKED)
                    .bookingId(bookingId)
                    .build());
        }
        
        when(roomAvailabilityRepository.findByBookingId(bookingId)).thenReturn(bookedAvailabilities);
        when(roomAvailabilityRepository.saveAll(anyList())).thenReturn(bookedAvailabilities);
        
        // Act
        boolean result = roomAvailabilityService.processCheckIn(bookingId);
        
        // Assert
        assertTrue(result);
        
        ArgumentCaptor<List<RoomAvailability>> captor = ArgumentCaptor.forClass(List.class);
        verify(roomAvailabilityRepository).saveAll(captor.capture());
        
        List<RoomAvailability> savedAvailabilities = captor.getValue();
        assertEquals(bookedAvailabilities.size(), savedAvailabilities.size());
        
        // Verify all availabilities were updated to OCCUPIED
        for (RoomAvailability availability : savedAvailabilities) {
            assertEquals(RoomStatus.OCCUPIED, availability.getStatus());
        }
    }
    
    @Test
    void processCheckOut_ShouldMarkRoomForCleaning() {
        // Arrange
        String bookingId = "test-booking";
        LocalDate today = LocalDate.now();
        
        Room room = Room.builder()
                .id(1L)
                .roomNumber("101")
                .status(RoomStatus.AVAILABLE)
                .build();
        
        List<RoomAvailability> occupiedAvailabilities = Arrays.asList(
            RoomAvailability.builder()
                .id(1L)
                .room(room)
                .date(today)
                .status(RoomStatus.OCCUPIED)
                .bookingId(bookingId)
                .build(),
            RoomAvailability.builder()
                .id(2L)
                .room(room)
                .date(today.plusDays(1))
                .status(RoomStatus.OCCUPIED)
                .bookingId(bookingId)
                .build()
        );
        
        when(roomAvailabilityRepository.findByBookingId(bookingId)).thenReturn(occupiedAvailabilities);
        when(roomAvailabilityRepository.saveAll(anyList())).thenReturn(occupiedAvailabilities);
        
        // Act
        boolean result = roomAvailabilityService.processCheckOut(bookingId);
        
        // Assert
        assertTrue(result);
        
        ArgumentCaptor<List<RoomAvailability>> captor = ArgumentCaptor.forClass(List.class);
        verify(roomAvailabilityRepository).saveAll(captor.capture());
        
        List<RoomAvailability> savedAvailabilities = captor.getValue();
        assertEquals(occupiedAvailabilities.size(), savedAvailabilities.size());
        
        // Verify all availabilities were updated to need cleaning
        for (RoomAvailability availability : savedAvailabilities) {
            assertEquals(RoomStatus.CLEANING, availability.getStatus());
            assertTrue(availability.isNeedsCleaning());
        }
    }
    
    @Test
    void markRoomAsCleaned_ShouldUpdateStatus() {
        // Arrange
        Long roomId = 1L;
        when(roomAvailabilityRepository.markRoomAsClean(roomId)).thenReturn(2);
        
        // Act
        boolean result = roomAvailabilityService.markRoomAsCleaned(roomId);
        
        // Assert
        assertTrue(result);
        verify(roomAvailabilityRepository).markRoomAsClean(roomId);
    }
    
    @Test
    void calculateOccupancyRate_ShouldReturnCorrectPercentage() {
        // Arrange
        Long propertyId = 1L;
        LocalDate date = LocalDate.now();
        
        when(roomAvailabilityRepository.countBookedOrOccupiedRoomsByPropertyAndDate(propertyId, date)).thenReturn(3L);
        when(roomAvailabilityRepository.countTotalRoomsByPropertyAndDate(propertyId, date)).thenReturn(4L);
        
        // Act
        double occupancyRate = roomAvailabilityService.calculateOccupancyRate(propertyId, date);
        
        // Assert
        assertEquals(75.0, occupancyRate);
    }
    
    @Test
    void calculateOccupancyRate_WhenNoRooms_ShouldReturnZero() {
        // Arrange
        Long propertyId = 1L;
        LocalDate date = LocalDate.now();
        
        when(roomAvailabilityRepository.countBookedOrOccupiedRoomsByPropertyAndDate(propertyId, date)).thenReturn(0L);
        when(roomAvailabilityRepository.countTotalRoomsByPropertyAndDate(propertyId, date)).thenReturn(0L);
        
        // Act
        double occupancyRate = roomAvailabilityService.calculateOccupancyRate(propertyId, date);
        
        // Assert
        assertEquals(0.0, occupancyRate);
    }
    
    @Test
    void isRoomAvailable_ShouldUseCacheForPerformance() {
        // Arrange
        Long roomId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        String cacheKey = "avail_" + roomId + "_" + startDate + "_" + endDate;
        
        Map<String, Boolean> availabilityCache = new HashMap<>();
        availabilityCache.put(cacheKey, true);
        ReflectionTestUtils.setField(roomAvailabilityService, "availabilityCache", availabilityCache);
        
        // Act
        boolean result = roomAvailabilityService.isRoomAvailable(roomId, startDate, endDate);
        
        // Assert
        assertTrue(result);
        verify(roomAvailabilityRepository, never()).findByRoomIdAndDateBetween(anyLong(), any(), any());
    }
}
