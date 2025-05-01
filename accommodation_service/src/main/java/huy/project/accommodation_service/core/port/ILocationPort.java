package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.LocationEntity;

import java.util.List;

public interface ILocationPort {
    LocationEntity save(LocationEntity location);
    LocationEntity getLocationById(Long id);
    void deleteLocationById(Long id);

    List<LocationEntity> getLocationsByIds(List<Long> ids);
}
