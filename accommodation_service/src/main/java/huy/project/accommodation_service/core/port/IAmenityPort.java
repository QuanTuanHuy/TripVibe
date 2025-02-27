package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.AmenityEntity;

import java.util.List;

public interface IAmenityPort {
    AmenityEntity save(AmenityEntity amenity);
    AmenityEntity getAmenityByName(String name);
    List<AmenityEntity> getAmenitiesByGroupId(Long groupId);
    List<AmenityEntity> getAmenitiesByGroupIds(List<Long> groupIds);
    AmenityEntity getAmenityById(Long id);
    void deleteAmenityById(Long id);
}
