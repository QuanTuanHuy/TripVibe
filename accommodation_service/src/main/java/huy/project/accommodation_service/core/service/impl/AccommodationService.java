package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.*;
import huy.project.accommodation_service.core.domain.dto.response.AccommodationDto;
import huy.project.accommodation_service.core.domain.dto.response.AccommodationThumbnail;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.service.IAccommodationService;
import huy.project.accommodation_service.core.usecase.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccommodationService implements IAccommodationService {
    CreateAccommodationUseCase createAccommodationUseCase;
    GetAccommodationUseCase getAccommodationUseCase;
    UpdateAccommodationUseCase updateAccommodationUseCase;

    UpdateUnitUseCase updateUnitUseCase;
    AddUnitUseCase addUnitUseCase;
    DeleteUnitUseCase deleteUnitUseCase;
    RestoreUnitUseCase restoreUnitUseCase;

    @Override
    public AccommodationEntity createAccommodation(Long userId, CreateAccommodationDto req) {
        return createAccommodationUseCase.createAccommodation(userId, req);
    }

    @Override
    public AccommodationEntity createAccommodationV2(Long userId, CreateAccommodationDtoV2 req, List<MultipartFile> images) {
        return createAccommodationUseCase.createAccommodationV2(userId, req, images);
    }

    @Override
    public AccommodationEntity getDetailAccommodation(Long id) {
        var accommodation = getAccommodationUseCase.getDetailAccommodation(id);
        getAccommodationUseCase.pushTouristViewHistory(id);
        return accommodation;
    }

    @Override
    public void addUnitToAccommodation(Long userId, Long accId, CreateUnitDto req) {
        addUnitUseCase.addUnit(userId, accId, req);
    }

    @Override
    public void addUnitToAccommodationV2(Long userId, Long accId, CreateUnitDtoV2 req, List<MultipartFile> files) {
        addUnitUseCase.addUnitV2(userId, accId, req, files);
    }

    @Override
    public void updateUnitImage(Long userId, Long accId, Long unitId, UpdateUnitImageDto req) {
        updateUnitUseCase.updateUnitImage(userId, accId, unitId, req);
    }

    @Override
    public void updateAccAmenity(Long userId, Long accId, UpdateAccAmenityDto req) {
        updateAccommodationUseCase.updateAccAmenity(userId, accId, req);
    }

    @Override
    public void updateUnitAmenity(Long userId, Long accId, Long unitId, UpdateUnitAmenityDto req) {
        updateUnitUseCase.updateUnitAmenity(userId, accId, unitId, req);
    }

    @Override
    public void updateUnitPriceGroup(Long userId, Long accId, Long unitId, UpdateUnitPriceGroupDto req) {
        updateUnitUseCase.updateUnitPriceGroup(userId, accId, unitId, req);
    }

    @Override
    public void updateUnitPriceCalendar(Long userId, Long accId, Long unitId, UpdateUnitPriceCalendarDto req) {
        updateUnitUseCase.updateUnitPriceCalendar(userId, accId, unitId, req);
    }

    @Override
    public void deleteUnit(Long userId, Long accId, Long unitId) {
        deleteUnitUseCase.deleteUnit(userId, accId, unitId);
    }

    @Override
    public void restoreUnit(Long userId, Long accId, Long unitId) {
        restoreUnitUseCase.restoreUnit(userId, accId, unitId);
    }

    @Override
    public AccommodationDto getAccDtoById(Long id) {
        return getAccommodationUseCase.getAccDtoById(id);
    }

    @Override
    public List<AccommodationDto> getAccommodations(AccommodationParams params) {
        return getAccommodationUseCase.getAccommodations(params);
    }

    @Override
    public List<AccommodationThumbnail> getAccommodationThumbnails(AccommodationThumbnailParams params) {
        return getAccommodationUseCase.getAccommodationThumbnails(params);
    }
}
