package huy.project.inventory_service.core.service;

import huy.project.inventory_service.core.domain.dto.request.AccommodationLockRequest;
import huy.project.inventory_service.core.domain.dto.request.CancelBookingRequest;
import huy.project.inventory_service.core.domain.dto.request.ConfirmBookingRequest;
import huy.project.inventory_service.core.domain.dto.request.ConfirmBookingResponse;
import huy.project.inventory_service.core.domain.dto.response.AccommodationLockResponse;
import huy.project.inventory_service.core.domain.dto.response.CancelBookingResponse;

public interface IInventoryService {
    /**
     * Lock rooms for a booking request.
     *
     * @param request the lock request containing accommodation and unit details
     * @return response with lock ID and status
     */
    AccommodationLockResponse lockRoomsForBooking(AccommodationLockRequest request);

    /**
     * Confirms a booking by updating the room status from temporarily locked to booked.
     */
    ConfirmBookingResponse confirmBooking(ConfirmBookingRequest request);

    /**
     * Release a previously created lock.
     *
     * @param lockId the lock ID to release
     * @return true if successfully released, false otherwise
     */
    boolean releaseLock(String lockId);

    CancelBookingResponse cancelBooking(CancelBookingRequest request);
}
