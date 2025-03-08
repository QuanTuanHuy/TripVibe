package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAccAmenityDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateUnitImageDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;

public interface IAccommodationService {
    AccommodationEntity createAccommodation(Long userId, CreateAccommodationDto req);
    AccommodationEntity getDetailAccommodation(Long id);
    void deleteAccommodation(Long id);
    void updateUnitImage(Long userId, Long accId, Long unitId, UpdateUnitImageDto req);
    void updateAccAmenity(Long userId, Long accId, UpdateAccAmenityDto req);
}
