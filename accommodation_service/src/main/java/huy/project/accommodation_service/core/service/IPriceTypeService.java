package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;

import java.util.List;

public interface IPriceTypeService {
    List<PriceTypeEntity> getALlPriceTypes();
}
