package huy.project.profile_service.core.port;

import huy.project.profile_service.core.domain.entity.TouristEntity;

public interface ITouristPort {
    TouristEntity save(TouristEntity tourist);
    TouristEntity getTouristById(Long id);
}
