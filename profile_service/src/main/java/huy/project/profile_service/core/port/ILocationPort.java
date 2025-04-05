package huy.project.profile_service.core.port;

import huy.project.profile_service.core.domain.entity.LocationEntity;

import java.util.List;

public interface ILocationPort {
    LocationEntity save(LocationEntity location);
    LocationEntity getLocationById(Long id);
    List<LocationEntity> getLocationsByIds(List<Long> ids);
    void deleteLocationById(Long id);
}
