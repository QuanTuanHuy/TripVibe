package huy.project.profile_service.core.port;

import huy.project.profile_service.core.domain.entity.TouristEntity;

import java.util.List;

public interface ITouristPort {
    TouristEntity save(TouristEntity tourist);
    TouristEntity getTouristById(Long id);
    List<TouristEntity> getTouristsByIds(List<Long> ids);
}
