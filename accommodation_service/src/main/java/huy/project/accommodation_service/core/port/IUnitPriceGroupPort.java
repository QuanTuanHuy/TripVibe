package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.UnitPriceGroupEntity;

import java.util.List;

public interface IUnitPriceGroupPort {
    List<UnitPriceGroupEntity> saveAll(List<UnitPriceGroupEntity> priceGroups);
    List<UnitPriceGroupEntity> getPriceGroupsByUnitIds(List<Long> unitIds);
    List<UnitPriceGroupEntity> getPriceGroupsByUnitId(Long unitId);
}
