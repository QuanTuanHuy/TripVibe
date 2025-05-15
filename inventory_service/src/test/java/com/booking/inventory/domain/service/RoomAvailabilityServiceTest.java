package com.booking.inventory.domain.service;

import com.booking.inventory.domain.model.*;
import com.booking.inventory.domain.repository.RoomAvailabilityRepository;
import com.booking.inventory.domain.repository.RoomRepository;
import com.booking.inventory.infrastructure.redis.RoomLockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomAvailabilityServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomAvailabilityRepository roomAvailabilityRepository;

    @Mock
    private RoomLockRepository roomLockRepository;

    @InjectMocks
    private RoomAvailabilityService roomAvailabilityService;

    private Room room;
    private RoomType roomType;
    private Property property;
    private RoomAvailability availability;

    @BeforeEach
    void setUp() {
        // Setup test data
        property = Property.builder()
                .id(1L)
                .name("Test Hotel")
                .build();

        roomType = RoomType.builder()
                .id(1L)
                .name("Standard Room")
                .basePrice(new BigDecimal("100.00"))
                .build();

        room = Room.builder()
                .id(1L)
                .roomNumber("101")
                .name("Standard Room 101")
                .property(property)
                .roomType(roomType)
                .status(RoomStatus.AVAILABLE)
                .build();

        availability = RoomAvailability.builder()
                .id(1L)
                .room(room)
                .date(LocalDate.now())
                .status(RoomStatus.AVAILABLE)
                .price(new BigDecimal("100.00"))
                .build();
    }

    @Test
    void isRoomAvailable_WhenAllDatesAreAvailable_ReturnsTrue() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        
        List<RoomAvailability> availabilities = Arrays.asList(
                RoomAvailability.builder().id(1L).room(room).date(startDate).status(RoomStatus.AVAILABLE).build(),
                RoomAvailability.builder().id(2L).room(room).date(startDate.plusDays(1)).status(RoomStatus.AVAILABLE).build(),
                RoomAvailability.builder().id(3L).room(room).date(startDate.plusDays(2)).status(RoomStatus.AVAILABLE).build()
        );
        
        when(roomAvailabilityRepository.findByRoomIdAndDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(availabilities);

        // Act
        boolean result = roomAvailabilityService.isRoomAvailable(1L, startDate, endDate);

        // Assert
        assertTrue(result);
        verify(roomAvailabilityRepository).findByRoomIdAndDateBetween(1L, startDate, endDate);
    }

    @Test
    void isRoomAvailable_WhenSomeDatesAreNotAvailable_ReturnsFalse() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        
        List<RoomAvailability> availabilities = Arrays.asList(
                RoomAvailability.builder().id(1L).room(room).date(startDate).status(RoomStatus.AVAILABLE).build(),
                RoomAvailability.builder().id(2L).room(room).date(startDate.plusDays(1)).status(RoomStatus.BOOKED).build(),
                RoomAvailability.builder().id(3L).room(room).date(startDate.plusDays(2)).status(RoomStatus.AVAILABLE).build()
        );
        
        when(roomAvailabilityRepository.findByRoomIdAndDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(availabilities);

        // Act
        boolean result = roomAvailabilityService.isRoomAvailable(1L, startDate, endDate);

        // Assert
        assertFalse(result);
        verify(roomAvailabilityRepository).findByRoomIdAndDateBetween(1L, startDate, endDate);
    }

    @Test
    void isRoomAvailable_WhenNotAllDatesHaveAvailabilityRecords_ReturnsFalse() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        
        // Only two availability records when we need three
        List<RoomAvailability> availabilities = Arrays.asList(
                RoomAvailability.builder().id(1L).room(room).date(startDate).status(RoomStatus.AVAILABLE).build(),
                RoomAvailability.builder().id(2L).room(room).date(startDate.plusDays(1)).status(RoomStatus.AVAILABLE).build()
        );
        
        when(roomAvailabilityRepository.findByRoomIdAndDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(availabilities);

        // Act
        boolean result = roomAvailabilityService.isRoomAvailable(1L, startDate, endDate);

        // Assert
        assertFalse(result);
        verify(roomAvailabilityRepository).findByRoomIdAndDateBetween(1L, startDate, endDate);
    }

    @Test
    void lockRoom_WhenRoomIsAvailable_ReturnsTrue() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate;
        String sessionId = "test-session";
          // Mock room availability
        when(roomAvailabilityRepository.findByRoomIdAndDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(availability));
        
        when(roomAvailabilityRepository.findByRoomIdAndDate(anyLong(), any(LocalDate.class)))
                .thenReturn(Optional.of(availability));
        
        when(roomLockRepository.save(any(RoomLock.class)))
                .thenReturn(new RoomLock());
        
        when(roomAvailabilityRepository.save(any(RoomAvailability.class)))
                .thenReturn(availability);

        // Act
        boolean result = roomAvailabilityService.lockRoom(1L, startDate, endDate, sessionId);

        // Assert
        assertTrue(result);
        verify(roomLockRepository).save(any(RoomLock.class));
        verify(roomAvailabilityRepository).save(any(RoomAvailability.class));
    }

    @Test
    void releaseRoomLock_ShouldReleaseLocksAndUpdateAvailability() {
        // Arrange
        String sessionId = "test-session";
        RoomLock roomLock = new RoomLock();
        roomLock.setRoomId(1L);
        roomLock.setDate(LocalDate.now());
        roomLock.setSessionId(sessionId);
          when(roomLockRepository.findBySessionId(sessionId))
                .thenReturn(Collections.singletonList(roomLock));
        
        when(roomAvailabilityRepository.findByRoomIdAndDate(anyLong(), any(LocalDate.class)))
                .thenReturn(Optional.of(availability));
        
        doNothing().when(roomLockRepository).deleteBySessionId(anyString());

        // Act
        roomAvailabilityService.releaseRoomLock(sessionId);

        // Assert
        verify(roomLockRepository).findBySessionId(sessionId);
        verify(roomAvailabilityRepository).findByRoomIdAndDate(1L, LocalDate.now());
        verify(roomAvailabilityRepository).save(any(RoomAvailability.class));
        verify(roomLockRepository).deleteBySessionId(sessionId);
    }

    @Test
    void confirmBooking_WhenLockedBySession_ReturnsTrue() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate;
        String sessionId = "test-session";
        String bookingId = "test-booking";
        
        RoomLock roomLock = new RoomLock();
        roomLock.setRoomId(1L);
        roomLock.setDate(LocalDate.now());
        roomLock.setSessionId(sessionId);
        
        when(roomLockRepository.findBySessionId(sessionId))
                .thenReturn(Collections.singletonList(roomLock));
        
        when(roomLockRepository.findByRoomIdAndDate(anyLong(), any(LocalDate.class)))
                .thenReturn(Optional.of(roomLock));
        
        when(roomAvailabilityRepository.findByRoomIdAndDate(anyLong(), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(availability));

        // Act
        boolean result = roomAvailabilityService.confirmBooking(1L, startDate, endDate, sessionId, bookingId);

        // Assert
        assertTrue(result);
        verify(roomAvailabilityRepository).save(any(RoomAvailability.class));
        verify(roomLockRepository).deleteBySessionId(sessionId);
    }

    @Test
    void cancelBooking_ShouldUpdateAvailability() {
        // Arrange
        String bookingId = "test-booking";
        availability.setBookingId(bookingId);
        availability.setStatus(RoomStatus.BOOKED);
        
        when(roomAvailabilityRepository.findAll())
                .thenReturn(Collections.singletonList(availability));

        // Act
        roomAvailabilityService.cancelBooking(bookingId);

        // Assert
        verify(roomAvailabilityRepository).save(any(RoomAvailability.class));
        assertEquals(RoomStatus.AVAILABLE, availability.getStatus());
        assertNull(availability.getBookingId());
    }

    @Test
    void initializeRoomAvailability_ShouldCreateAvailabilityRecords() {
        // Arrange
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(2);
        
        when(roomRepository.findById(anyLong()))
                .thenReturn(Optional.of(room));
        
        when(roomAvailabilityRepository.findByRoomIdAndDate(anyLong(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        // Act
        roomAvailabilityService.initializeRoomAvailability(1L, startDate, endDate);

        // Assert
        verify(roomRepository).findById(1L);
        verify(roomAvailabilityRepository, times(3)).findByRoomIdAndDate(eq(1L), any(LocalDate.class));
        verify(roomAvailabilityRepository, times(3)).save(any(RoomAvailability.class));
    }
}
