package huy.project.profile_service.core.port;

import huy.project.profile_service.core.domain.entity.LocationEntity;

public interface ILocationPort {
    LocationEntity save(LocationEntity location);
    void deleteLocationById(Long id);
}
