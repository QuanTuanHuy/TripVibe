package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.UnitInventoryEntity;

import java.time.LocalDate;
import java.util.List;

public interface IUnitInventoryPort {
    List<UnitInventoryEntity> saveAll(List<UnitInventoryEntity> unitInventories);
    List<UnitInventoryEntity> getInventoriesByUnitIdAndDateRange(Long unitId, LocalDate startDate, LocalDate endDate);
    UnitInventoryEntity getInventoryByUnitIdAndDate(Long unitId, LocalDate date);
}
