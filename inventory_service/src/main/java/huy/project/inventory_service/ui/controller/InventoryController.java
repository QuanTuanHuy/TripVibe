package huy.project.inventory_service.ui.controller;

import huy.project.inventory_service.core.domain.dto.request.*;
import huy.project.inventory_service.core.domain.dto.response.AccommodationLockResponse;
import huy.project.inventory_service.core.domain.dto.response.CancelBookingResponse;
import huy.project.inventory_service.core.domain.dto.response.CheckInResponse;
import huy.project.inventory_service.core.domain.dto.response.CheckOutResponse;
import huy.project.inventory_service.core.service.IInventoryService;
import huy.project.inventory_service.kernel.util.AuthenUtils;
import huy.project.inventory_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {
    private final IInventoryService inventoryService;

    @PostMapping("/lock")
    public ResponseEntity<Resource<AccommodationLockResponse>> lockRoomsForBooking(
            @RequestBody AccommodationLockRequest request
    ) {
        log.info("Received request to lock rooms for accommodation: {}", request.getAccommodationId());
        return ResponseEntity.ok(new Resource<>(
                inventoryService.lockRoomsForBooking(request)));
    }

    @DeleteMapping("/release_lock/{lockId}")
    public ResponseEntity<Resource<?>> releaseLock(@PathVariable String lockId) {
        log.info("Received request to release lock: {}", lockId);
        return ResponseEntity.ok(new Resource<>(
                inventoryService.releaseLock(lockId)));
    }

    @PutMapping("/confirm_booking")
    public ResponseEntity<Resource<?>> confirmBooking(@RequestBody ConfirmBookingRequest request) {
        log.info("Received request to confirm booking: {}, with lock: {}",
                request.getBookingId(), request.getLockId());
        Long userId = AuthenUtils.getCurrentUserId();
        request.setUserId(userId);
        log.info("User ID: {}", userId);
        return ResponseEntity.ok(new Resource<>(
                inventoryService.confirmBooking(request)));
    }

    @PutMapping("/cancel_booking")
    public ResponseEntity<Resource<CancelBookingResponse>> cancelBooking(@RequestBody CancelBookingRequest request) {
        log.info("Received request to cancel booking: {}", request.getBookingId());
        return ResponseEntity.ok(new Resource<>(
                inventoryService.cancelBooking(request)));
    }

    @PostMapping("/checkin")
    public ResponseEntity<Resource<CheckInResponse>> checkIn(@RequestBody CheckInRequest request) {
        Long userId = AuthenUtils.getCurrentUserId();
        log.info("Received request to check in user: {}", userId);
        request.setGuestId(userId);
        return ResponseEntity.ok(new Resource<>(inventoryService.checkIn(request)));
    }

    @PostMapping("/checkout")
    public ResponseEntity<Resource<CheckOutResponse>> checkOut(@RequestBody CheckOutRequest request) {
        Long userId = AuthenUtils.getCurrentUserId();
        log.info("Received request to check out user: {}", userId);
        request.setGuestId(userId);
        return ResponseEntity.ok(new Resource<>(inventoryService.checkOut(request)));
    }
}
