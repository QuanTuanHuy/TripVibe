package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.AccommodationAmenityEntity;

import java.util.List;

public interface IAccommodationAmenityPort {
    List<AccommodationAmenityEntity> saveAll(List<AccommodationAmenityEntity> accommodationAmenities);
    List<AccommodationAmenityEntity> getAccAmenitiesByAccId(Long accId);
    void deleteByAccIdAndAmenities(Long accId, List<Long> amenityIds);
}
