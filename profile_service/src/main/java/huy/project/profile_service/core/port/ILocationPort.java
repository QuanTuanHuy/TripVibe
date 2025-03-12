package huy.project.profile_service.core.port;

import huy.project.profile_service.core.domain.entity.LocationEntity;

public interface ILocationPort {
    LocationEntity save(LocationEntity location);
    LocationEntity getLocationById(Long id);
    void deleteLocationById(Long id);
}
