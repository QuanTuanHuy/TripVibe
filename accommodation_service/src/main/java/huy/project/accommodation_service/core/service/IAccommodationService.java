package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.*;
import huy.project.accommodation_service.core.domain.dto.response.AccommodationDto;
import huy.project.accommodation_service.core.domain.dto.response.AccommodationThumbnail;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IAccommodationService {
    AccommodationEntity createAccommodationV2(Long userId, CreateAccommodationDtoV2 req, List<MultipartFile> images);
    AccommodationEntity getDetailAccommodation(Long id);
    void addUnitToAccommodationV2(Long userId, Long accId, CreateUnitDtoV2 req, List<MultipartFile> files);
    void updateUnitImage(Long userId, Long accId, Long unitId, DeleteImageDto deleteImageDto, List<MultipartFile> newImages);
    void updateAccAmenity(Long userId, Long accId, UpdateAccAmenityDto req);
    void updateUnitAmenity(Long userId, Long accId, Long unitId, UpdateUnitAmenityDto req);
    void updateUnitPriceGroup(Long userId, Long accId, Long unitId, UpdateUnitPriceGroupDto req);
    void updateUnitPriceCalendar(Long userId, Long accId, Long unitId, UpdateUnitPriceCalendarDto req);
    void deleteUnit(Long userId, Long accId, Long unitId);
    void restoreUnit(Long userId, Long accId, Long unitId);
    AccommodationDto getAccDtoById(Long id);
    List<AccommodationDto> getAccommodations(AccommodationParams params);

    List<AccommodationThumbnail> getAccommodationThumbnails(AccommodationThumbnailParams params);
}
