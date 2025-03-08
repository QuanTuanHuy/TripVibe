package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.constant.ImageEntityType;
import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationAmenityDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAccAmenityDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateUnitImageDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationAmenityEntity;
import huy.project.accommodation_service.core.domain.entity.ImageEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IAccommodationAmenityPort;
import huy.project.accommodation_service.core.port.IImagePort;
import huy.project.accommodation_service.core.validation.AccommodationValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateAccommodationUseCase {
    private final GetUnitUseCase getUnitUseCase;
    private final GetAccommodationAmenityUseCase getAccAmenityUseCase;

    private final IImagePort imagePort;
    private final IAccommodationAmenityPort accAmenityPort;

    private final AccommodationValidation accValidation;

    @Transactional(rollbackFor = Exception.class)
    public void updateUnitImage(Long userId, Long accId, Long unitId, UpdateUnitImageDto req) {
        if (!accValidation.accommodationExistToHost(userId, accId)) {
            log.error("Accommodation with id {} not found or not belong to user {}", accId, userId);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        var unit = getUnitUseCase.getUnitByAccIdAndId(accId, unitId);
        if (unit == null) {
            log.error("Unit with id {} not found", unitId);
            throw new AppException(ErrorCode.UNIT_NOT_FOUND);
        }

        // delete images
        List<ImageEntity> existedImages = imagePort.getImagesByEntityIdAndType(unitId, ImageEntityType.UNIT.getType());
        var existedImageIds = existedImages.stream().map(ImageEntity::getId).collect(Collectors.toSet());
        if (!existedImageIds.containsAll(req.getDeleteImageIds())) {
            log.error("Some image ids not found in unit {}", unitId);
            throw new AppException(ErrorCode.UNIT_IMAGE_NOT_FOUND);
        }

        imagePort.deleteImagesByIds(req.getDeleteImageIds());

        // save new images
        var newImages = req.getNewImages().stream()
                .map(image -> ImageEntity.builder()
                        .entityId(unitId)
                        .entityType(ImageEntityType.UNIT.getType())
                        .url(image.getUrl())
                        .isPrimary(image.getIsPrimary())
                        .build())
                .toList();
        imagePort.saveAll(newImages);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAccAmenity(Long userId, Long accId, UpdateAccAmenityDto req) {
        if (!accValidation.accommodationExistToHost(userId, accId)) {
            log.error("Accommodation with id {} not found or not belong to user {}", accId, userId);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        List<AccommodationAmenityEntity> existedAccAmenities = getAccAmenityUseCase.getAccAmenitiesByAccId(accId);
        var existedAmenityIds = existedAccAmenities.stream()
                .map(AccommodationAmenityEntity::getAmenityId).collect(Collectors.toSet());

        // delete amenities
        List<Long> allDeleteAmenityIds = new ArrayList<>(req.getDeleteAmenityIds());
        if (!existedAmenityIds.containsAll(req.getDeleteAmenityIds())) {
            log.error("Some amenity ids not found in accommodation {}", accId);
            throw new AppException(ErrorCode.ACCOMMODATION_AMENITY_NOT_FOUND);
        }

        // delete amenities for update
        allDeleteAmenityIds.addAll(req.getNewAmenities().stream()
                .map(CreateAccommodationAmenityDto::getAmenityId)
                .filter(existedAmenityIds::contains)
                .toList());
        if (!CollectionUtils.isEmpty(allDeleteAmenityIds)) {
            accAmenityPort.deleteByAccIdAndAmenities(accId, allDeleteAmenityIds);
        }

        // save new amenities
        var newAmenities = req.getNewAmenities().stream()
                .map(dto -> AccommodationAmenityEntity.builder()
                        .accommodationId(accId)
                        .amenityId(dto.getAmenityId())
                        .fee(dto.getFee())
                        .needToReserve(dto.getNeedToReserve())
                        .build())
                .toList();
        if (!CollectionUtils.isEmpty(newAmenities)) {
            accAmenityPort.saveAll(newAmenities);
        }
    }
}
