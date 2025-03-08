package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ImageEntityType;
import huy.project.accommodation_service.core.domain.dto.request.CreateUnitDto;
import huy.project.accommodation_service.core.domain.entity.*;
import huy.project.accommodation_service.core.domain.mapper.UnitMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.*;
import huy.project.accommodation_service.core.validation.UnitValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateUnitUseCase {
    private final IUnitPort unitPort;
    private final IUnitPriceTypePort unitPriceTypePort;
    private final IUnitAmenityPort unitAmenityPort;
    private final IUnitPriceGroupPort unitPriceGroupPort;
    private final IImagePort imagePort;

    private final CreateBedroomUseCase createBedroomUseCase;

    private final UnitValidation unitValidation;

    @Transactional(rollbackFor = Exception.class)
    public UnitEntity createUnit(Long accommodationId, CreateUnitDto req) {
        // validate request
        var validationResult = unitValidation.validateCreateUnitDto(req);
        if (!validationResult.getFirst()) {
            log.error("create unit failed: {}", validationResult.getSecond());
            throw new AppException(validationResult.getSecond());
        }

        // create main unit
        UnitEntity unit = UnitMapper.INSTANCE.toEntity(accommodationId, req);
        unit = unitPort.save(unit);
        final Long unitId = unit.getId();

        // create unit amenities
        if (!CollectionUtils.isEmpty(req.getAmenities())) {
            List<UnitAmenityEntity> unitAmenities = req.getAmenities().stream()
                    .map(amenity -> UnitAmenityEntity.builder()
                            .unitId(unitId)
                            .amenityId(amenity.getAmenityId())
                            .fee(amenity.getFee())
                            .needToReserve(amenity.getNeedToReserve())
                            .build())
                    .toList();
            unitAmenityPort.saveAll(unitAmenities);
        }

        // create unit price types
        if (!CollectionUtils.isEmpty(req.getPriceTypes())) {
            List<UnitPriceTypeEntity> unitPriceTypes = req.getPriceTypes().stream()
                    .map(priceType -> UnitPriceTypeEntity.builder()
                            .unitId(unitId)
                            .priceTypeId(priceType.getPriceTypeId())
                            .percentage(priceType.getPercentage())
                            .build())
                    .toList();
            unitPriceTypePort.saveAll(unitPriceTypes);
        }

        // create unit price groups
        if (!CollectionUtils.isEmpty(req.getPriceGroups())) {
            List<UnitPriceGroupEntity> unitPriceGroups = req.getPriceGroups().stream()
                    .map(priceGroup -> UnitPriceGroupEntity.builder()
                            .unitId(unitId)
                            .numberOfGuests(priceGroup.getNumberOfGuests())
                            .percentage(priceGroup.getPercentage())
                            .build())
                    .toList();
            unitPriceGroupPort.saveAll(unitPriceGroups);
        }

        // create unit images
        if (!CollectionUtils.isEmpty(req.getImages())) {
            List<ImageEntity> unitImages = req.getImages().stream()
                    .map(image -> ImageEntity.builder()
                            .entityId(unitId)
                            .entityType(ImageEntityType.UNIT.getType())
                            .url(image.getUrl())
                            .isPrimary(image.getIsPrimary())
                            .build())
                    .toList();
            imagePort.saveAll(unitImages);
        }

        // create bedrooms
        req.getBedrooms().forEach(bedroom ->
                createBedroomUseCase.createBedroom(unitId, bedroom));

        // need to create unit price calendar asynchronously
        return unit;
    }
}
