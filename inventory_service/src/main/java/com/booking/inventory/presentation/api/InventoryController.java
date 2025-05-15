package com.booking.inventory.presentation.api;

import com.booking.inventory.application.dto.AvailabilitySearchRequest;
import com.booking.inventory.application.dto.BookingConfirmationRequest;
import com.booking.inventory.application.dto.RoomAvailabilityDto;
import com.booking.inventory.application.dto.RoomLockRequest;
import com.booking.inventory.application.service.InventoryApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory API", description = "API for room inventory and availability management")
public class InventoryController {
    
    private final InventoryApplicationService inventoryService;
    
    @PostMapping("/search")
    @Operation(summary = "Search for available rooms", description = "Search for available rooms based on property, room type, dates, and occupancy")
    public ResponseEntity<List<RoomAvailabilityDto>> searchAvailability(@RequestBody AvailabilitySearchRequest request) {
        List<RoomAvailabilityDto> availabilities = inventoryService.searchAvailability(request);
        return ResponseEntity.ok(availabilities);
    }
    
    @PostMapping("/lock")
    @Operation(summary = "Lock a room", description = "Temporarily lock a room during the booking process")
    public ResponseEntity<Boolean> lockRoom(@RequestBody RoomLockRequest request) {
        boolean success = inventoryService.lockRoom(request);
        if (success) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }
    
    @DeleteMapping("/lock/{sessionId}")
    @Operation(summary = "Release a room lock", description = "Release a temporary lock on a room")
    public ResponseEntity<Void> releaseRoomLock(@PathVariable String sessionId) {
        inventoryService.releaseRoomLock(sessionId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/confirm")
    @Operation(summary = "Confirm a booking", description = "Confirm a booking for a room")
    public ResponseEntity<Boolean> confirmBooking(@RequestBody BookingConfirmationRequest request) {
        boolean success = inventoryService.confirmBooking(request);
        if (success) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }
    
    @DeleteMapping("/booking/{bookingId}")
    @Operation(summary = "Cancel a booking", description = "Cancel a booking for a room")
    public ResponseEntity<Void> cancelBooking(@PathVariable String bookingId) {
        inventoryService.cancelBooking(bookingId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/check-in/{bookingId}")
    @Operation(summary = "Process check-in", description = "Process check-in for a booking")
    public ResponseEntity<Void> processCheckIn(@PathVariable String bookingId) {
        inventoryService.processCheckIn(bookingId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/check-out/{bookingId}")
    @Operation(summary = "Process check-out", description = "Process check-out for a booking")
    public ResponseEntity<Void> processCheckOut(@PathVariable String bookingId) {
        inventoryService.processCheckOut(bookingId);
        return ResponseEntity.ok().build();
    }
}
