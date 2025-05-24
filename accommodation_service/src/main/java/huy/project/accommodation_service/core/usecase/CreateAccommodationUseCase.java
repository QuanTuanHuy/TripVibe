package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import huy.project.accommodation_service.core.domain.dto.request.AccommodationParams;
import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationDtoV2;
import huy.project.accommodation_service.core.domain.entity.*;
import huy.project.accommodation_service.core.domain.kafka.*;
import huy.project.accommodation_service.core.domain.kafka.inventory.SyncAccommodationDto;
import huy.project.accommodation_service.core.domain.mapper.AccommodationMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.*;
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
public class CreateAccommodationUseCase {
    private final IAccommodationPort accommodationPort;
    private final IAccommodationAmenityPort accommodationAmenityPort;
    private final IAccommodationLanguagePort accLanguagePort;
    private final ICachePort cachePort;

    private final CreateLocationUseCase createLocationUseCase;
    private final CreateUnitUseCase createUnitUseCase;
    private final GetAccommodationUseCase getAccommodationUseCase;

    private final IKafkaPublisher kafkaPublisher;

    private final AccommodationValidation accValidation;

    @Transactional(rollbackFor = Exception.class)
    public AccommodationEntity createAccommodationV2(Long userId, CreateAccommodationDtoV2 req, List<MultipartFile> images) {
        // validate req
        var validationResult = accValidation.validateCreateAccommodationDto(req);
        if (!validationResult.getFirst()) {
            log.error("create accommodation failed: {}", validationResult.getSecond());
            throw new AppException(validationResult.getSecond());
        }

        // create location
        LocationEntity location = createLocationUseCase.createLocation(req.getLocation());

        // create main accommodation
        AccommodationEntity accommodation = AccommodationMapper.INSTANCE.toEntity(userId, location.getId(), req);
        accommodation = accommodationPort.save(accommodation);
        final Long accommodationId = accommodation.getId();

        // create accommodation amenities
        List<AccommodationAmenityEntity> accAmenities = req.getAmenityIds().stream()
                .map(amenityId -> AccommodationAmenityEntity.builder()
                        .accommodationId(accommodationId)
                        .amenityId(amenityId)
                        .build())
                .toList();
        accommodationAmenityPort.saveAll(accAmenities);

        // create accommodation languages
        List<AccommodationLanguageEntity> accLanguages = req.getLanguageIds().stream()
                .map(languageId -> AccommodationLanguageEntity.builder()
                        .accommodationId(accommodationId)
                        .languageId(languageId)
                        .build())
                .toList();
        accLanguagePort.saveAll(accLanguages);

        // create accommodation units
        var unit = createUnitUseCase.createUnitV2(accommodationId, req.getUnit(), images);

        // chose the first image as primary
        var firstImage = unit.getImages().get(0).getUrl();
        accommodation.setThumbnailUrl(firstImage);

        handleAfterCreateAccommodation(accommodationId);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetAccommodations(AccommodationParams.builder()
                        .hostId(userId)
                .build()));

        return accommodationPort.save(accommodation);
    }

    public void handleAfterCreateAccommodation(Long accId) {
        var accommodation = getAccommodationUseCase.getDetailAccommodation(accId);

        // create accommodation in booking service
        var message = CreateAccommodationMessage.from(accommodation);

        // create accommodation in search service
        var messageElastic = CreateAccElasticMessage.from(accommodation);

        // create accommodation in inventory service
        var syncAccDto = SyncAccommodationDto.from(accommodation);

        // create rating summary
        var ratingSummaryMessage = CreateRatingSummaryMessage.builder()
                .accommodationId(accId)
                .build();

        kafkaPublisher.pushAsync(message.toKafkaBaseDto(), TopicConstant.BookingCommand.TOPIC, "");
        kafkaPublisher.pushAsync(messageElastic.toKafkaBaseDto(), TopicConstant.SearchCommand.TOPIC, "");
        kafkaPublisher.pushAsync(ratingSummaryMessage.toKafkaBaseDto(), TopicConstant.RatingCommand.TOPIC, "");
        kafkaPublisher.pushAsync(syncAccDto.toKafkaBaseDto(), TopicConstant.InventoryCommand.TOPIC, "");
    }

}
