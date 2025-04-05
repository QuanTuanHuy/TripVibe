package huy.project.accommodation_service.core.validation;

import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;
import huy.project.accommodation_service.core.port.IPriceTypePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceTypeValidation {
    private final IPriceTypePort priceTypePort;

    public boolean priceTypesExist(List<Long> ids) {
        List<PriceTypeEntity> priceTypes = priceTypePort.getAllPriceTypes();
        return priceTypes.stream().map(PriceTypeEntity::getId).allMatch(ids::contains);
    }
}
