package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ImageEntityType;
import huy.project.accommodation_service.core.domain.dto.request.CreateUnitDtoV2;
import huy.project.accommodation_service.core.domain.dto.response.FileResourceResponse;
import huy.project.accommodation_service.core.domain.entity.*;
import huy.project.accommodation_service.core.domain.mapper.UnitMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.*;
import huy.project.accommodation_service.core.port.client.IFileStoragePort;
import huy.project.accommodation_service.core.validation.UnitValidation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CreateUnitUseCase {
    IUnitPort unitPort;
    IUnitPriceTypePort unitPriceTypePort;
    IUnitAmenityPort unitAmenityPort;
    IUnitPriceGroupPort unitPriceGroupPort;
    IImagePort imagePort;
    IFileStoragePort fileStoragePort;

    CreateBedroomUseCase createBedroomUseCase;
    UnitValidation unitValidation;

//    @Transactional(rollbackFor = Exception.class)
//    public UnitEntity createUnit(Long accommodationId, CreateUnitDto req) {
//        // validate request
//        var validationResult = unitValidation.validateCreateUnitDto(req);
//        if (!validationResult.getFirst()) {
//            log.error("create unit failed: {}", validationResult.getSecond());
//            throw new AppException(validationResult.getSecond());
//        }
//
//        // create main unit
//        UnitEntity unit = UnitMapper.INSTANCE.toEntity(accommodationId, req);
//        unit = unitPort.save(unit);
//        final Long unitId = unit.getId();
//
//        // create unit amenities
//        if (!CollectionUtils.isEmpty(req.getAmenities())) {
//            List<UnitAmenityEntity> unitAmenities = req.getAmenities().stream()
//                    .map(amenity -> UnitAmenityEntity.builder()
//                            .unitId(unitId)
//                            .amenityId(amenity.getAmenityId())
//                            .fee(amenity.getFee())
//                            .needToReserve(amenity.getNeedToReserve())
//                            .build())
//                    .toList();
//            unitAmenityPort.saveAll(unitAmenities);
//        }
//
//        // create unit price types
//        if (!CollectionUtils.isEmpty(req.getPriceTypes())) {
//            List<UnitPriceTypeEntity> unitPriceTypes = req.getPriceTypes().stream()
//                    .map(priceType -> UnitPriceTypeEntity.builder()
//                            .unitId(unitId)
//                            .priceTypeId(priceType.getPriceTypeId())
//                            .percentage(priceType.getPercentage())
//                            .build())
//                    .toList();
//            unitPriceTypePort.saveAll(unitPriceTypes);
//        }
//
//        // create unit price groups
//        if (!CollectionUtils.isEmpty(req.getPriceGroups())) {
//            List<UnitPriceGroupEntity> unitPriceGroups = req.getPriceGroups().stream()
//                    .map(priceGroup -> UnitPriceGroupEntity.builder()
//                            .unitId(unitId)
//                            .numberOfGuests(priceGroup.getNumberOfGuests())
//                            .percentage(priceGroup.getPercentage())
//                            .build())
//                    .toList();
//            unitPriceGroupPort.saveAll(unitPriceGroups);
//        }
//
//        // create unit images
//        if (!CollectionUtils.isEmpty(req.getImages())) {
//            List<ImageEntity> unitImages = req.getImages().stream()
//                    .map(image -> ImageEntity.builder()
//                            .id(image.getId())
//                            .entityId(unitId)
//                            .entityType(ImageEntityType.UNIT.getType())
//                            .url(image.getUrl())
//                            .isPrimary(image.getIsPrimary())
//                            .build())
//                    .toList();
//            imagePort.saveAll(unitImages);
//        }
//
//        // create bedrooms
//        req.getBedrooms().forEach(bedroom ->
//                createBedroomUseCase.createBedroom(unitId, bedroom));
//
//        // need to create unit price calendar asynchronously
//        return unit;
//    }

    @Transactional(rollbackFor = Exception.class)
    public UnitEntity createUnitV2(Long accommodationId, CreateUnitDtoV2 req, List<MultipartFile> images) {
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
        if (!CollectionUtils.isEmpty(req.getAmenityIds())) {
            List<UnitAmenityEntity> unitAmenities = req.getAmenityIds().stream()
                    .map(amenityId -> UnitAmenityEntity.builder()
                            .unitId(unitId)
                            .amenityId(amenityId)
                            .build())
                    .toList();
            unit.setAmenities(unitAmenityPort.saveAll(unitAmenities));
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
            unit.setPriceTypes(unitPriceTypePort.saveAll(unitPriceTypes));
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
            unit.setPriceGroups(unitPriceGroupPort.saveAll(unitPriceGroups));
        }

        // create unit images
        if (!CollectionUtils.isEmpty(images)) {
            List<FileResourceResponse> fileResponse = fileStoragePort.uploadFiles(
                    images, unit.getId().toString(), UnitEntity.class.toString());
            List<ImageEntity> unitImages = fileResponse.stream()
                    .map(image -> ImageEntity.builder()
                            .id(image.getId())
                            .entityId(unitId)
                            .entityType(ImageEntityType.UNIT.getType())
                            .url(image.getUrl())
                            .versions(image.getVersions())
                            .isPrimary(false)
                            .build())
                    .toList();
            unit.setImages(imagePort.saveAll(unitImages));
        }

        // create bedrooms
        req.getBedrooms().forEach(bedroom ->
                createBedroomUseCase.createBedroom(unitId, bedroom));

        // need to create unit price calendar asynchronously
        return unit;
    }
}
