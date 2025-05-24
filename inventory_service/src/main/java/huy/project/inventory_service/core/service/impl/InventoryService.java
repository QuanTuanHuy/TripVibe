package huy.project.inventory_service.core.service.impl;

import huy.project.inventory_service.core.domain.dto.request.*;
import huy.project.inventory_service.core.domain.dto.response.AccommodationLockResponse;
import huy.project.inventory_service.core.domain.dto.response.CancelBookingResponse;
import huy.project.inventory_service.core.domain.dto.response.CheckInResponse;
import huy.project.inventory_service.core.domain.dto.response.CheckOutResponse;
import huy.project.inventory_service.core.service.IInventoryService;
import huy.project.inventory_service.core.usecase.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService implements IInventoryService {
    private final ConfirmBookingUseCase confirmBookingUseCase;
    private final LockRoomsUseCase lockRoomsUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;
    private final LockTransactionManagerUseCase lockTransactionManagerUseCase;
    private final CheckInUseCase checkInUseCase;
    private final CheckOutUseCase checkOutUseCase;

    @Override
    public AccommodationLockResponse lockRoomsForBooking(AccommodationLockRequest request) {
        log.info("Processing lock request for accommodation: {}", request.getAccommodationId());
        return lockRoomsUseCase.execute(request);
    }

    @Override
    public ConfirmBookingResponse confirmBooking(ConfirmBookingRequest request) {
        return confirmBookingUseCase.confirmBooking(request);
    }

    @Override
    public boolean releaseLock(String lockId) {
        log.info("Releasing lock: {}", lockId);
        return lockTransactionManagerUseCase.releaseLock(lockId);
    }

    @Override
    public CancelBookingResponse cancelBooking(CancelBookingRequest request) {
        return cancelBookingUseCase.cancelBooking(request);
    }

    @Override
    public CheckInResponse checkIn(CheckInRequest request) {
        return checkInUseCase.checkIn(request);
    }

    @Override
    public CheckOutResponse checkOut(CheckOutRequest request) {
        return checkOutUseCase.checkOut(request);
    }

}
