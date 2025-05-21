package huy.project.inventory_service.ui.controller;

import huy.project.inventory_service.core.domain.dto.request.AccommodationLockRequest;
import huy.project.inventory_service.core.domain.dto.request.CancelBookingRequest;
import huy.project.inventory_service.core.domain.dto.request.ConfirmBookingRequest;
import huy.project.inventory_service.core.domain.dto.response.AccommodationLockResponse;
import huy.project.inventory_service.core.domain.dto.response.CancelBookingResponse;
import huy.project.inventory_service.core.service.IInventoryService;
import huy.project.inventory_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventory")
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
        return ResponseEntity.ok(new Resource<>(
                inventoryService.confirmBooking(request)));
    }

    @PutMapping("/cancel_booking")
    public ResponseEntity<Resource<CancelBookingResponse>> cancelBooking(@RequestBody CancelBookingRequest request) {
        log.info("Received request to cancel booking: {}", request.getBookingId());
        return ResponseEntity.ok(new Resource<>(
                inventoryService.cancelBooking(request)));
    }
}
