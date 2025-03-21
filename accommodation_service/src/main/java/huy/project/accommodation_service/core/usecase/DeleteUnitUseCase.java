package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import huy.project.accommodation_service.core.domain.kafka.UnitDeletedEvent;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.ICachePort;
import huy.project.accommodation_service.core.port.IKafkaPublisher;
import huy.project.accommodation_service.core.port.IUnitPort;
import huy.project.accommodation_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteUnitUseCase {
    private final IUnitPort unitPort;
    private final ICachePort cachePort;
    private final IKafkaPublisher kafkaPublisher;

    private final GetAccommodationUseCase getAccommodationUseCase;

    @Transactional(rollbackFor = Exception.class)
    public void deleteUnit(Long userId, Long accId, Long unitId) {
        validateRequest(userId, accId, unitId);

        var unit = unitPort.getUnitByAccIdAndId(accId, unitId);
        unit.setIsDeleted(true);

        unitPort.save(unit);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetAccommodationById(accId));

        // publish event to kafka
        publishEventToKafka(accId, unitId);
    }

    private void validateRequest(Long userId, Long accId, Long unitId) {
        var accommodation = getAccommodationUseCase.getAccommodationById(accId);
        if (accommodation == null) {
            log.error("Accommodation with id {} not found", accId);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }
        if (!accommodation.getHostId().equals(userId)) {
            log.error("Accommodation with id {} not belong to user {}", accId, userId);
            throw new AppException(ErrorCode.FORBIDDEN_DELETE_UNIT);
        }

        var unit = unitPort.getUnitByAccIdAndId(accId, unitId);
        if (unit == null) {
            log.error("Unit with id {} not found in accommodation {}", unitId, accId);
            throw new AppException(ErrorCode.UNIT_NOT_FOUND);
        }
        if (unit.getIsDeleted() != null && unit.getIsDeleted()) {
            log.error("Unit with id {} already deleted", unitId);
            throw new AppException(ErrorCode.UNIT_ALREADY_DELETED);
        }
    }

    private void publishEventToKafka(Long accId, Long unitId) {
        var event = UnitDeletedEvent.builder()
                .accommodationId(accId)
                .unitId(unitId)
                .build();
        kafkaPublisher.pushAsync(event.toDomainEventDto(), TopicConstant.AccommodationEvent.TOPIC, null);
    }
}
