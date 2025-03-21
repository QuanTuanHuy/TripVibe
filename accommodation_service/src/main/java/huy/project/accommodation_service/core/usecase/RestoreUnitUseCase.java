package huy.project.accommodation_service.core.usecase;

import org.springframework.stereotype.Service;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.ICachePort;
import huy.project.accommodation_service.core.port.IUnitPort;
import huy.project.accommodation_service.kernel.utils.CacheUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestoreUnitUseCase {
    private final IUnitPort unitPort;
    private final ICachePort cachePort;

    private final GetAccommodationUseCase getAccommodationUseCase;

    private final AddUnitUseCase addUnitUseCase;

    @Transactional(rollbackOn = Exception.class)
    public void restoreUnit(Long userId, Long accId, Long unitId) {
        validateRequest(userId, accId , unitId);
        
        var unit = unitPort.getUnitByAccIdAndId(accId, unitId);
        unit.setIsDeleted(null);

        unitPort.save(unit);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetAccommodationById(accId));

        // push message to kafka
        addUnitUseCase.pushMessageToKafka(accId, unitId);
    }

    private void validateRequest(Long userId, Long accId, Long unitId) {
        var accommodation = getAccommodationUseCase.getAccommodationById(accId);
        if (accommodation == null) {
            log.error("Accommodation with id {} not found", accId);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }
        if (!accommodation.getHostId().equals(userId)) {
            log.error("Accommodation with id {} not belong to user {}", accId, userId);
            throw new AppException(ErrorCode.FORBIDDEN_RESTORE_UNIT);
        }

        var unit = unitPort.getUnitByAccIdAndId(accId, unitId);
        if (unit == null) {
            log.error("Unit with id {} not found in accommodation {}", unitId, accId);
            throw new AppException(ErrorCode.UNIT_NOT_FOUND);
        }
        if (unit.getIsDeleted() == null || !unit.getIsDeleted()) {
            log.error("Unit with id {} not deleted", unitId);
            throw new AppException(ErrorCode.UNIT_NOT_DELETED);
        }
    }
}
