package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.UpdateUnitInventoryRequest;
import huy.project.accommodation_service.core.domain.entity.UnitInventoryEntity;

import java.time.LocalDate;
import java.util.List;

public interface IUnitInventoryService {
    List<UnitInventoryEntity> getUnitInventoryForUnit(Long unitId, LocalDate startDate, LocalDate endDate);
    List<UnitInventoryEntity> blockInventory(Long userId, Long unitId, LocalDate startDate, LocalDate endDate, String reason);
    List<UnitInventoryEntity> updateInventory(Long userId, Long unitId, List<UpdateUnitInventoryRequest> updates, String source);
}
