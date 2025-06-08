package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.CreatePriceTypeDto;
import huy.project.accommodation_service.core.domain.dto.request.PriceTypeParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IPriceTypeService {
    Pair<PageInfo, List<PriceTypeEntity>> getPriceTypes(PriceTypeParams params);
    PriceTypeEntity createPriceType(CreatePriceTypeDto req);
    void createIfNotExists(List<CreatePriceTypeDto> priceTypes);
}
