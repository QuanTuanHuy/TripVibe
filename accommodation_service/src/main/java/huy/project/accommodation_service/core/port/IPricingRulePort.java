package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.PricingRuleEntity;

public interface IPricingRulePort {
    PricingRuleEntity save(PricingRuleEntity pricingRule);
    PricingRuleEntity getPricingRuleById(Long id);
    void deletePricingRule(Long id);
}
