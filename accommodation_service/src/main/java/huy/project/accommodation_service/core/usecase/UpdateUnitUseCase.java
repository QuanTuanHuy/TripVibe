package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.constant.ImageEntityType;
import huy.project.accommodation_service.core.domain.constant.TopicConstant;
import huy.project.accommodation_service.core.domain.dto.request.*;
import huy.project.accommodation_service.core.domain.dto.response.FileResourceResponse;
import huy.project.accommodation_service.core.domain.entity.*;
import huy.project.accommodation_service.core.domain.kafka.DeleteFileMessage;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.*;
import huy.project.accommodation_service.core.port.client.IFileStoragePort;
import huy.project.accommodation_service.core.validation.AccommodationValidation;
import huy.project.accommodation_service.kernel.utils.CacheUtils;
import huy.project.accommodation_service.kernel.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUnitUseCase {
    private final GetUnitUseCase getUnitUseCase;
    private final GetUnitAmenityUseCase getUnitAmenityUseCase;
    private final GetUnitPriceGroupUseCase getUnitPriceGroupUseCase;

    private final IImagePort imagePort;
    private final IUnitAmenityPort unitAmenityPort;
    private final IUnitPriceGroupPort unitPriceGroupPort;
    private final IUnitPriceCalendarPort unitPriceCalendarPort;
    private final IFileStoragePort fileStoragePort;
    private final ICachePort cachePort;

    private final IKafkaPublisher kafkaPublisher;

    private final AccommodationValidation accValidation;

    @Transactional(rollbackFor = Exception.class)
    public void updateUnitImage(Long userId, Long accId, Long unitId, DeleteImageDto deleteImageDto, List<MultipartFile> newImages) {
        validateUpdateUnit(userId, accId, unitId);

        // delete images

        if (deleteImageDto != null && !CollectionUtils.isEmpty(deleteImageDto.getImageIds())) {
            List<Long> deleteImageIds = deleteImageDto.getImageIds();
            List<ImageEntity> existedImages = imagePort.getImagesByEntityIdAndType(unitId, ImageEntityType.UNIT.getType());
            var existedImageIds = existedImages.stream().map(ImageEntity::getId).collect(Collectors.toSet());
            if (!existedImageIds.containsAll(deleteImageIds)) {
                log.error("Some image ids not found in unit {}", unitId);
                throw new AppException(ErrorCode.UNIT_IMAGE_NOT_FOUND);
            }

            imagePort.deleteImagesByIds(deleteImageIds);

            pushToKafka(userId, deleteImageIds);
        }

        // save new images
        if (!CollectionUtils.isEmpty(newImages)) {
            List<FileResourceResponse> uploadedFiles = fileStoragePort
                    .uploadFiles(newImages, unitId.toString(), UnitEntity.class.toString());
            List<ImageEntity> newImageEntities = uploadedFiles.stream()
                    .map(file -> ImageEntity.builder()
                            .id(file.getId())
                            .entityId(unitId)
                            .entityType(ImageEntityType.UNIT.getType())
                            .url(file.getUrl())
                            .versions(file.getVersions())
                            .isPrimary(false)
                            .build())
                    .toList();
            imagePort.saveAll(newImageEntities);
        }

        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetAccommodationById(accId));
    }

    private void pushToKafka(Long userId, List<Long> imageIds) {
        var deleteFileMessage = DeleteFileMessage.builder()
                .userId(userId)
                .ids(imageIds)
                .build();
        kafkaPublisher.pushAsync(deleteFileMessage.toKafkaBaseDto(), TopicConstant.FileCommand.TOPIC, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUnitAmenity(Long userId, Long accId, Long unitId, UpdateUnitAmenityDto req) {
        validateUpdateUnit(userId, accId, unitId);

        // delete amenities
        var existedUnitAmenities = getUnitAmenityUseCase.getUnitAmenitiesByUnitId(unitId);
        var existedAmenityIds = existedUnitAmenities.stream()
                .map(unitAmenity -> unitAmenity.getAmenity().getId())
                .collect(Collectors.toSet());

        var allDeleteAmenityIds = new ArrayList<>(req.getDeletedAmenityIds());
        if (!existedAmenityIds.containsAll(req.getDeletedAmenityIds())) {
            log.error("Some amenity ids not found in unit {}", unitId);
            throw new AppException(ErrorCode.UNIT_AMENITY_NOT_FOUND);
        }

        // delete amenities for update
        allDeleteAmenityIds.addAll(req.getNewAmenities().stream()
                .map(CreateUnitAmenityDto::getAmenityId)
                .filter(existedAmenityIds::contains)
                .toList());

        if (!allDeleteAmenityIds.isEmpty()) {
            unitAmenityPort.deleteByUnitIdAndAmenityIdIn(unitId, allDeleteAmenityIds);
        }

        // create new amenities
        List<UnitAmenityEntity> newAmenities = req.getNewAmenities().stream()
                .map(dto -> UnitAmenityEntity.builder()
                        .unitId(unitId)
                        .amenityId(dto.getAmenityId())
                        .fee(dto.getFee())
                        .needToReserve(dto.getNeedToReserve())
                        .build())
                .toList();
        if (!newAmenities.isEmpty()) {
            unitAmenityPort.saveAll(newAmenities);
        }

        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetAccommodationById(accId));

        // need to sync with search service
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUnitPriceGroup(Long userId, Long accId, Long unitId, UpdateUnitPriceGroupDto req) {
        validateUpdateUnit(userId, accId, unitId);

        List<UnitPriceGroupEntity> priceGroups = getUnitPriceGroupUseCase.getUnitPriceGroupsByUnitId(unitId);

        priceGroups.forEach(priceGroup ->
            req.getNewPriceGroups().stream()
                    .filter(dto -> dto.getNumberOfGuests().equals(priceGroup.getNumberOfGuests()))
                    .findFirst()
                    .ifPresent(newPriceGroup -> priceGroup.setPercentage(newPriceGroup.getPercentage()))
        );

        unitPriceGroupPort.saveAll(priceGroups);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUnitPriceCalendar(Long userId, Long accId, Long unitId, UpdateUnitPriceCalendarDto req) {
        validateUpdateUnit(userId, accId, unitId);

        LocalDate startDate = DateTimeUtils.convertUnixToLocalDate(req.getStartDate());
        LocalDate endDate = DateTimeUtils.convertUnixToLocalDate(req.getEndDate());

        // delete old price calendars
        unitPriceCalendarPort.deletePriceByUnitIdAndDate(unitId, startDate, endDate);

        // save new price calendars
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        List<UnitPriceCalendarEntity> priceCalendars = dates.stream()
                .map(date -> UnitPriceCalendarEntity.builder()
                        .unitId(unitId)
                        .date(date)
                        .basePrice(req.getPrice())
                        .build())
                .toList();
        unitPriceCalendarPort.saveAll(priceCalendars);
    }

    private void validateUpdateUnit(Long userId, Long accId, Long unitId) {
        if (!accValidation.accommodationExistToHost(userId, accId)) {
            log.error("Accommodation with id {} not found or not belong to user {}", accId, userId);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        var unit = getUnitUseCase.getUnitByAccIdAndId(accId, unitId);
        if (unit == null) {
            log.error("Unit with id {} not found", unitId);
            throw new AppException(ErrorCode.UNIT_NOT_FOUND);
        }
    }
}
