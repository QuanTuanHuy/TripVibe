package huy.project.inventory_service.core.service;

import huy.project.inventory_service.core.domain.dto.request.AccommodationLockRequest;
import huy.project.inventory_service.core.domain.dto.response.AccommodationLockResponse;

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
     *
     * @param lockId    The lock identifier associated with the booking
     * @param bookingId The booking ID to associate with the rooms
     * @return True if the booking was successfully confirmed
     */
    boolean confirmBooking(String lockId, Long bookingId);

    /**
     * Release a previously created lock.
     *
     * @param lockId the lock ID to release
     * @return true if successfully released, false otherwise
     */
    boolean releaseLock(String lockId);
}
