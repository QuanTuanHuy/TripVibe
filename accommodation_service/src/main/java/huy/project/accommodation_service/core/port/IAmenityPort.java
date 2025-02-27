package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.AmenityEntity;

public interface IAmenityPort {
    AmenityEntity save(AmenityEntity amenity);
    AmenityEntity getAmenityByName(String name);
}
