package com.booking.inventory.monitoring;

import com.booking.inventory.domain.model.*;
import com.booking.inventory.domain.repository.RoomAvailabilityRepository;
import com.booking.inventory.domain.repository.RoomRepository;
import com.booking.inventory.domain.service.RoomAvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class InventoryPerformanceTest {

    @Autowired
    private RoomAvailabilityService roomAvailabilityService;
    
    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @SpyBean
    private RoomAvailabilityRepository spyRoomAvailabilityRepository;
    
    private Room room;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        
        // Create room and availability for performance testing
        RoomType roomType = new RoomType();
        roomType.setName("Performance Test Room Type");
        roomType.setDescription("For performance testing");
        roomType.setMaxOccupancy(2);
        roomType.setBasePrice(new BigDecimal("100.00"));
        
        Property property = new Property();
        property.setName("Performance Test Property");
        
        room = new Room();
        room.setRoomNumber("P101");
        room.setName("Performance Test Room");
        room.setRoomType(roomType);
        room.setProperty(property);
        room.setStatus(RoomStatus.AVAILABLE);
        
        roomRepository.save(room);
        
        // Initialize room availability for 30 days
        roomAvailabilityService.initializeRoomAvailability(room.getId(), today, today.plusDays(30));
    }

    @Test
    void testCachingPerformance() {
        Long roomId = room.getId();
        LocalDate startDate = today.plusDays(3);
        LocalDate endDate = today.plusDays(5);
        
        // First call should hit the database
        long startTime = System.nanoTime();
        boolean firstResult = roomAvailabilityService.isRoomAvailable(roomId, startDate, endDate);
        long firstCallTime = System.nanoTime() - startTime;
        
        // Second call should use cache
        startTime = System.nanoTime();
        boolean secondResult = roomAvailabilityService.isRoomAvailable(roomId, startDate, endDate);
        long secondCallTime = System.nanoTime() - startTime;
        
        // Verify both calls return same result
        assertTrue(firstResult);
        assertTrue(secondResult);
        
        // Second call should be significantly faster (at least 10x)
        // But we'll use a smaller factor for test reliability
        assertTrue(secondCallTime * 2 < firstCallTime, 
                "Cached call should be at least 2x faster than database call");
        
        System.out.println("First call time: " + firstCallTime / 1_000_000.0 + "ms");
        System.out.println("Second call time: " + secondCallTime / 1_000_000.0 + "ms");
        System.out.println("Speed improvement: " + (double)firstCallTime / secondCallTime + "x");
    }
    
    @Test
    void testOptimisticLockingWithConcurrentBookings() throws InterruptedException {
        Long roomId = room.getId();
        LocalDate bookingDate = today.plusDays(10);
        int numberOfThreads = 5;
        
        // Set up a latch to make threads start at the same time
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        
        // This will count successful bookings
        int[] successCount = new int[1];
        
        for (int i = 0; i < numberOfThreads; i++) {
            int threadId = i;
            executor.submit(() -> {
                try {
                    // Wait for signal to start
                    latch.await();
                    
                    String sessionId = "test-session-" + threadId;
                    String bookingId = "test-booking-" + threadId;
                    
                    // Try to lock and book the same room
                    boolean lockResult = roomAvailabilityService.lockRoom(roomId, bookingDate, bookingDate, sessionId);
                    
                    if (lockResult) {
                        boolean confirmResult = roomAvailabilityService.confirmBooking(roomId, bookingDate, bookingDate, 
                                sessionId, bookingId);
                        if (confirmResult) {
                            synchronized (successCount) {
                                successCount[0]++;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Thread " + threadId + " failed: " + e.getMessage());
                }
            });
        }
        
        // Start all threads simultaneously
        latch.countDown();
        
        // Wait for all threads to complete
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        // Only one booking should succeed due to optimistic locking
        assertEquals(1, successCount[0], "Only one booking should succeed for the same room and date");
    }
    
    @Test
    void testBatchOperationPerformance() {
        Long roomId = room.getId();
        LocalDate startDate = today.plusDays(15);
        LocalDate endDate = today.plusDays(20);
        String sessionId = UUID.randomUUID().toString();
        String bookingId = UUID.randomUUID().toString();
        
        // Count database operations for a multi-day booking
        int dayCount = (int)(java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate)) + 1;
        
        // Use spy to monitor database calls
        reset(spyRoomAvailabilityRepository);
        
        // Execute booking process
        roomAvailabilityService.lockRoom(roomId, startDate, endDate, sessionId);
        roomAvailabilityService.confirmBooking(roomId, startDate, endDate, sessionId, bookingId);
        
        // Verify batch operations are used efficiently
        // The saveAll method should be called a limited number of times, not once per day
        verify(spyRoomAvailabilityRepository, atMost(4)).saveAll(anyList());
    }
}
