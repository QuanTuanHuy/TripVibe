package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.entity.UnitEntity;

import java.util.List;

public interface IUnitService {
    List<UnitEntity> getUnitsByIds(List<Long> ids);
}
