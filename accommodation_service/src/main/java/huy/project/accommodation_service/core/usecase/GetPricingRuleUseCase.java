package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.PricingRuleEntity;
import huy.project.accommodation_service.core.port.IPricingRulePort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetPricingRuleUseCase {
    IPricingRulePort pricingRulePort;

    public List<PricingRuleEntity> getPricingRulesByUnitId(Long unitId) {
        return pricingRulePort.getPricingRulesByUnitId(unitId);
    }
}
