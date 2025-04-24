package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.PricingRuleEntity;

import java.time.LocalDate;
import java.util.List;

public interface IPricingRulePort {
    PricingRuleEntity save(PricingRuleEntity pricingRule);
    PricingRuleEntity getPricingRuleById(Long id);
    void deletePricingRule(Long id);
    List<PricingRuleEntity> getPricingRulesByUnitId(Long unitId);
    List<PricingRuleEntity> getActiveRulesByUnitIdAndDateRange(Long unitId, LocalDate startDate, LocalDate endDate);
}
