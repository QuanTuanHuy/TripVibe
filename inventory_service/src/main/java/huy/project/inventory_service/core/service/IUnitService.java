package huy.project.inventory_service.core.service;

import huy.project.inventory_service.core.domain.dto.request.SyncUnitDto;
import huy.project.inventory_service.core.domain.entity.Unit;

public interface IUnitService {
    Unit syncUnit(SyncUnitDto request);
}
