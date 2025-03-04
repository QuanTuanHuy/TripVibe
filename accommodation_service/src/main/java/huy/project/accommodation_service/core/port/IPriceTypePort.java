package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;

import java.util.List;

public interface IPriceTypePort {
    PriceTypeEntity getPriceTypeByName(String name);
    PriceTypeEntity getPriceTypeById(Long id);
    List<PriceTypeEntity> getAllPriceTypes();
}
