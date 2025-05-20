package huy.project.inventory_service.core.service.impl;

import huy.project.inventory_service.core.domain.dto.request.AccommodationLockRequest;
import huy.project.inventory_service.core.domain.dto.response.AccommodationLockResponse;
import huy.project.inventory_service.core.service.IInventoryService;
import huy.project.inventory_service.core.usecase.ConfirmBookingUseCase;
import huy.project.inventory_service.core.usecase.LockRoomsUseCase;
import huy.project.inventory_service.core.usecase.LockTransactionManagerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService implements IInventoryService {
    private final ConfirmBookingUseCase confirmBookingUseCase;
    private final LockRoomsUseCase lockRoomsUseCase;
    private final LockTransactionManagerUseCase lockTransactionManagerUseCase;

    @Override
    public AccommodationLockResponse lockRoomsForBooking(AccommodationLockRequest request) {
        log.info("Processing lock request for accommodation: {}", request.getAccommodationId());
        return lockRoomsUseCase.execute(request);
    }

    @Override
    public boolean confirmBooking(String lockId, Long bookingId) {
        return confirmBookingUseCase.confirmBooking(lockId, bookingId);
    }

    @Override
    public boolean releaseLock(String lockId) {
        log.info("Releasing lock: {}", lockId);
        return lockTransactionManagerUseCase.releaseLock(lockId);
    }
}
