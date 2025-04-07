package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ImageEntityType;
import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationDto;
import huy.project.accommodation_service.core.domain.entity.*;
import huy.project.accommodation_service.core.domain.kafka.*;
import huy.project.accommodation_service.core.domain.mapper.AccommodationMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IAccommodationAmenityPort;
import huy.project.accommodation_service.core.port.IAccommodationLanguagePort;
import huy.project.accommodation_service.core.port.IAccommodationPort;
import huy.project.accommodation_service.core.port.IKafkaPublisher;
import huy.project.accommodation_service.core.validation.AccommodationValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateAccommodationUseCase {
    private final IAccommodationPort accommodationPort;
    private final IAccommodationAmenityPort accommodationAmenityPort;
    private final IAccommodationLanguagePort accLanguagePort;

    private final CreateLocationUseCase createLocationUseCase;
    private final CreateImageUseCase createImageUseCase;
    private final CreateUnitUseCase createUnitUseCase;
    private final GetAccommodationUseCase getAccommodationUseCase;

    private final IKafkaPublisher kafkaPublisher;

    private final AccommodationValidation accValidation;

    @Transactional(rollbackFor = Exception.class)
    public AccommodationEntity createAccommodation(Long userId, CreateAccommodationDto req) {
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
        List<AccommodationAmenityEntity> accAmenities = req.getAmenities().stream()
                .map(amenity -> AccommodationAmenityEntity.builder()
                        .accommodationId(accommodationId)
                        .amenityId(amenity.getAmenityId())
                        .fee(amenity.getFee())
                        .needToReserve(amenity.getNeedToReserve())
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

        // create accommodation images
        List<ImageEntity> accImages = req.getImages().stream()
                .map(image -> ImageEntity.builder()
                        .id(image.getId())
                        .entityType(ImageEntityType.ACCOMMODATION.getType())
                        .entityId(accommodationId)
                        .url(image.getUrl())
                        .isPrimary(image.getIsPrimary())
                        .build())
                .toList();
        createImageUseCase.createImages(accImages);

        // create accommodation units
        req.getUnits().forEach(unit -> createUnitUseCase.createUnit(accommodationId, unit));

        handleAfterCreateAccommodation(accommodationId);

        return accommodation;
    }

    public void handleAfterCreateAccommodation(Long accId) {
        var accommodation = getAccommodationUseCase.getDetailAccommodation(accId);

        // create accommodation in booking service
        var message = CreateAccommodationMessage.from(accommodation);

        // create accommodation in search service
        var messageElastic = CreateAccElasticMessage.from(accommodation);

        // create rating summary
        var ratingSummaryMessage = CreateRatingSummaryMessage.builder()
                .accommodationId(accId)
                .build();

        kafkaPublisher.pushAsync(message.toKafkaBaseDto(), TopicConstant.BookingCommand.TOPIC, "");
        kafkaPublisher.pushAsync(messageElastic.toKafkaBaseDto(), TopicConstant.SearchCommand.TOPIC, "");
        kafkaPublisher.pushAsync(ratingSummaryMessage.toKafkaBaseDto(), TopicConstant.RatingCommand.TOPIC, "");
    }

}
