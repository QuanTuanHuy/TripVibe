package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.UnitPriceTypeEntity;

import java.util.List;

public interface IUnitPriceTypePort {
    List<UnitPriceTypeEntity> saveAll(List<UnitPriceTypeEntity> unitPriceTypes);
}
