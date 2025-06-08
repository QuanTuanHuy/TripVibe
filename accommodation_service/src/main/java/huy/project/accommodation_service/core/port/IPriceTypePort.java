package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.dto.request.PriceTypeParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IPriceTypePort {
    PriceTypeEntity getPriceTypeByName(String name);
    PriceTypeEntity getPriceTypeById(Long id);
    List<PriceTypeEntity> getAllPriceTypes();
    PriceTypeEntity save(PriceTypeEntity priceType);
    long countAll();
    Pair<PageInfo, List<PriceTypeEntity>> getPriceTypes(PriceTypeParams params);
}
