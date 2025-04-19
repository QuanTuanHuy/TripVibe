package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import huy.project.accommodation_service.core.domain.dto.request.CreateUnitDto;
import huy.project.accommodation_service.core.domain.dto.request.CreateUnitDtoV2;
import huy.project.accommodation_service.core.domain.kafka.AddUnitToAccElasticMessage;
import huy.project.accommodation_service.core.domain.kafka.AddUnitToAccMessage;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.ICachePort;
import huy.project.accommodation_service.core.port.IKafkaPublisher;
import huy.project.accommodation_service.core.validation.AccommodationValidation;
import huy.project.accommodation_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddUnitUseCase {
    private final CreateUnitUseCase createUnitUseCase;
    private final GetUnitUseCase getUnitUseCase;

    private final ICachePort cachePort;
    private final IKafkaPublisher kafkaPublisher;

    private final AccommodationValidation accValidation;

    @Transactional(rollbackFor = Exception.class)
    public void addUnit(Long userId, Long accId, CreateUnitDto req) {
        if (!accValidation.accommodationExistToHost(userId, accId)) {
            log.error("Accommodation with id {} not found or not belong to user {}", accId, userId);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        var newUnit = createUnitUseCase.createUnit(accId, req);

        pushMessageToKafka(accId, newUnit.getId());

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetAccommodationById(accId));
    }

    @Transactional
    public void addUnitV2(Long userId, Long accId, CreateUnitDtoV2 req, List<MultipartFile> files) {
        if (!accValidation.accommodationExistToHost(userId, accId)) {
            log.error("Accommodation with id {} not found or not belong to user {}", accId, userId);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        var newUnit = createUnitUseCase.createUnitV2(accId, req, files);

        pushMessageToKafka(accId, newUnit.getId());

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetAccommodationById(accId));
    }

    public void pushMessageToKafka(Long accId, Long unitId) {
        var unit = getUnitUseCase.getUnitByAccIdAndId(accId, unitId);

        var message = AddUnitToAccMessage.from(unit);
        var messageElastic = AddUnitToAccElasticMessage.from(unit);

        kafkaPublisher.pushAsync(message.toKafkaBaseDto(), TopicConstant.BookingCommand.TOPIC, null);
        kafkaPublisher.pushAsync(messageElastic.toKafkaBaseDto(), TopicConstant.SearchCommand.TOPIC, null);
    }
}
