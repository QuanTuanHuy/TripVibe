package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.CacheConstant;
import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import huy.project.accommodation_service.core.domain.dto.request.AccommodationParams;
import huy.project.accommodation_service.core.domain.dto.request.AccommodationThumbnailParams;
import huy.project.accommodation_service.core.domain.dto.request.PriceCalculationRequest;
import huy.project.accommodation_service.core.domain.dto.response.AccommodationDto;
import huy.project.accommodation_service.core.domain.dto.response.AccommodationThumbnail;
import huy.project.accommodation_service.core.domain.dto.response.PriceCalculationResponse;
import huy.project.accommodation_service.core.domain.dto.response.RatingSummaryDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.domain.entity.LocationEntity;
import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import huy.project.accommodation_service.core.domain.kafka.CreateViewHistoryMessage;
import huy.project.accommodation_service.core.domain.mapper.UnitMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IAccommodationPort;
import huy.project.accommodation_service.core.port.ICachePort;
import huy.project.accommodation_service.core.port.IKafkaPublisher;
import huy.project.accommodation_service.core.port.ILocationPort;
import huy.project.accommodation_service.core.port.client.IRatingPort;
import huy.project.accommodation_service.kernel.utils.AuthenUtils;
import huy.project.accommodation_service.kernel.utils.CacheUtils;
import huy.project.accommodation_service.kernel.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAccommodationUseCase {
    private final IAccommodationPort accommodationPort;
    private final ILocationPort locationPort;
    private final ICachePort cachePort;
    private final IRatingPort ratingPort;

    private final GetLocationUseCase getLocationUseCase;
    private final GetUnitUseCase getUnitUseCase;
    private final GetAccommodationAmenityUseCase getAccAmenityUseCase;
    private final GetAccommodationLanguageUseCase getAccLanguageUseCase;
    private final GetAccommodationTypeUseCase getAccTypeUseCase;
    private final DynamicPricingUseCase dynamicPricingUseCase;

    private final JsonUtils jsonUtils;

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
        accommodation.setType(getAccTypeUseCase.getAccommodationTypeById(accommodation.getTypeId()));

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

    public List<AccommodationEntity> getAccommodations(AccommodationParams params) {
        // get from cache
        String cacheKey = CacheUtils.buildCacheKeyGetAccommodations(params);
        String cachedAccommodations = cachePort.getFromCache(cacheKey);
        if (StringUtils.hasText(cachedAccommodations)) {
            return jsonUtils.fromJsonList(cachedAccommodations, AccommodationEntity.class);
        }

        var accommodations = accommodationPort.getAccommodations(params);
        if (CollectionUtils.isEmpty(accommodations)) {
            return List.of();
        }

        accommodations.forEach(a -> {
            a.setLocation(locationPort.getLocationById(a.getLocationId()));
            a.setUnits(getUnitUseCase.getUnitsByAccommodationId(a.getId()));
            a.setAmenities(getAccAmenityUseCase.getAccAmenitiesByAccId(a.getId()));
            a.setLanguages(getAccLanguageUseCase.getLanguageByAccId(a.getId()));
            a.setType(getAccTypeUseCase.getAccommodationTypeById(a.getTypeId()));
        });

        // set to cache
        cachePort.setToCache(cacheKey, accommodations, CacheConstant.DEFAULT_TTL);

        return accommodations;
    }

    public List<AccommodationThumbnail> getAccommodationThumbnails(AccommodationThumbnailParams params) {
        if (CollectionUtils.isEmpty(params.getIds())) {
            return List.of();
        }

        // get accommodation
        var accommodations = accommodationPort.getAccommodationsByIds(params.getIds());
        if (CollectionUtils.isEmpty(accommodations)) {
            return List.of();
        }

        // get units
        List<UnitEntity> units = getUnitUseCase.getUnitsByAccIds(params.getIds());
        accommodations.forEach(acc -> {
            List<UnitEntity> accUnits = units.stream()
                    .filter(unit -> unit.getAccommodationId().equals(acc.getId()))
                    .collect(Collectors.toList());
            acc.setUnits(accUnits);
        });

        // get location
        List<Long> locationIds = accommodations.stream().map(AccommodationEntity::getLocationId).toList();
        List<LocationEntity> locations = locationPort.getLocationsByIds(locationIds);
        var locationMap = locations.stream().collect(Collectors.toMap(LocationEntity::getId, Function.identity()));

        accommodations.forEach(acc -> acc.setLocation(locationMap.get(acc.getLocationId())));

        // get rating summary
        var ratingSummaries = ratingPort.getRatingSummariesByAccIds(params.getIds());
        var ratingSummaryMap = ratingSummaries.stream()
                .collect(Collectors.toMap(RatingSummaryDto::getAccommodationId,
                        rs -> AccommodationThumbnail.RatingSummary.builder()
                                .rating(rs.getNumberOfRatings() > 0 ? (double) rs.getTotalRating() / rs.getNumberOfRatings(): 0)
                                .numberOfRatings(rs.getNumberOfRatings())
                                .build()));

        var accommodationThumbnails = accommodations.stream().map(a -> AccommodationThumbnail.builder()
                        .id(a.getId())
                        .name(a.getName())
                        .description(a.getDescription())
                        .thumbnailUrl(a.getThumbnailUrl())
                        .units(a.getUnits().stream().map(u -> AccommodationThumbnail.Unit.builder()
                                .id(u.getId())
                                .name(u.getUnitName().getName())
                                .description(u.getDescription())
                                .build()).toList())
                        .location(AccommodationThumbnail.Location.builder()
                                .countryId(a.getLocation().getCountryId())
                                .provinceId(a.getLocation().getProvinceId())
                                .address(a.getLocation().getDetailAddress())
                                .build())
                        .ratingSummary(ratingSummaryMap.get(a.getId()))
                        .build())
                .toList();

        // get dynamic pricing
        if (params.getGuestCount() != null && params.getStartDate() != null && params.getEndDate() != null) {
            accommodationThumbnails.forEach(a -> {
                Long firstUnitId = a.getUnits().get(0).getId();
                PriceCalculationResponse priceCalculation = dynamicPricingUseCase.calculatePrice(
                        PriceCalculationRequest.builder()
                                .unitId(firstUnitId)
                                .checkInDate(params.getStartDate())
                                .checkOutDate(params.getEndDate())
                                .guestCount(params.getGuestCount())
                                .build());

                int lengthOfStay = (int) ChronoUnit.DAYS.between(params.getStartDate(), params.getEndDate());
                a.setPriceInfo(AccommodationThumbnail.PriceInfo.builder()
                                .lengthOfStay(lengthOfStay)
                                .guestCount(params.getGuestCount())
                                .initialPrice(priceCalculation.getBasePrice().multiply(BigDecimal.valueOf(lengthOfStay)))
                                .currentPrice(priceCalculation.getTotalPrice())
                        .build());
            });
        }

        return accommodationThumbnails;
    }
}
