package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.*;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;

public interface IAccommodationService {
    AccommodationEntity createAccommodation(Long userId, CreateAccommodationDto req);
    AccommodationEntity getDetailAccommodation(Long id);
    void addUnitToAccommodation(Long userId, Long accId, CreateUnitDto req);
    void updateUnitImage(Long userId, Long accId, Long unitId, UpdateUnitImageDto req);
    void updateAccAmenity(Long userId, Long accId, UpdateAccAmenityDto req);
    void updateUnitAmenity(Long userId, Long accId, Long unitId, UpdateUnitAmenityDto req);
    void updateUnitPriceGroup(Long userId, Long accId, Long unitId, UpdateUnitPriceGroupDto req);
    void updateUnitPriceCalendar(Long userId, Long accId, Long unitId, UpdateUnitPriceCalendarDto req);
    void deleteUnit(Long userId, Long accId, Long unitId);
    void restoreUnit(Long userId, Long accId, Long unitId);
}
