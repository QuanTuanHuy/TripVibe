package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.CacheConstant;
import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import huy.project.accommodation_service.core.domain.dto.response.AccommodationDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.domain.kafka.CreateViewHistoryMessage;
import huy.project.accommodation_service.core.domain.mapper.UnitMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IAccommodationPort;
import huy.project.accommodation_service.core.port.ICachePort;
import huy.project.accommodation_service.core.port.IKafkaPublisher;
import huy.project.accommodation_service.kernel.utils.AuthenUtils;
import huy.project.accommodation_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAccommodationUseCase {
    private final IAccommodationPort accommodationPort;
    private final ICachePort cachePort;

    private final GetLocationUseCase getLocationUseCase;
    private final GetUnitUseCase getUnitUseCase;
    private final GetAccommodationAmenityUseCase getAccAmenityUseCase;
    private final GetAccommodationLanguageUseCase getAccLanguageUseCase;
    private final GetAccommodationTypeUseCase getAccTypeUseCase;

    private final IKafkaPublisher kafkaPublisher;

    public AccommodationEntity getDetailAccommodation(Long id) {
        // get from cache
        String cacheKey = CacheUtils.buildCacheKeyGetAccommodationById(id);
        AccommodationEntity cachedAccommodation = cachePort.getFromCache(cacheKey, AccommodationEntity.class);
        if (cachedAccommodation != null) {
            return cachedAccommodation;
        }

        AccommodationEntity accommodation = accommodationPort.getAccommodationById(id);
        if (accommodation == null) {
            log.error("Accommodation with id {} not found", id);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        accommodation.setLocation(getLocationUseCase.getLocationById(accommodation.getLocationId()));
        accommodation.setUnits(getUnitUseCase.getUnitsByAccommodationId(id));
        accommodation.setAmenities(getAccAmenityUseCase.getAccAmenitiesByAccId(id));
        accommodation.setLanguages(getAccLanguageUseCase.getLanguageByAccId(id));
        accommodation.setType(getAccTypeUseCase.getAccommodationTypeById(id));

        // set to cache
        cachePort.setToCache(cacheKey, accommodation, CacheConstant.DEFAULT_TTL);

        return accommodation;
    }

    public void pushTouristViewHistory(Long accId) {
        long userId = 0L;
        try {
            userId = AuthenUtils.getCurrentUserId();
        } catch (Exception e) {
            log.info("User not login, skip push view history");
        }

        if (userId == 0) {
            return;
        }
        CreateViewHistoryMessage message = CreateViewHistoryMessage.builder()
                .touristId(userId)
                .accommodationId(accId)
                .timestamp(Instant.now().toEpochMilli())
                .build();
        kafkaPublisher.pushAsync(message.toKafkaBaseDto(), TopicConstant.TouristCommand.TOPIC, null);
    }

    public AccommodationEntity getAccommodationById(Long id) {
        return accommodationPort.getAccommodationById(id);
    }

    public AccommodationDto getAccDtoById(Long id) {
        var accommodation = accommodationPort.getAccommodationById(id);
        if (accommodation == null) {
            log.error("Accommodation with id {} not found", id);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        return AccommodationDto.builder()
                .id(accommodation.getId())
                .hostId(accommodation.getHostId())
                .typeId(accommodation.getTypeId())
                .name(accommodation.getName())
                .isVerified(accommodation.getIsVerified())
                .units(getUnitUseCase.getUnitsByAccommodationId(id).stream().map(UnitMapper.INSTANCE::toDto).toList())
                .build();
    }
}
