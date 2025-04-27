package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.CreatePricingRuleDto;
import huy.project.accommodation_service.core.domain.dto.request.PriceCalculationRequest;
import huy.project.accommodation_service.core.domain.dto.request.PricingRuleParams;
import huy.project.accommodation_service.core.domain.dto.request.UpdateBasePriceRequest;
import huy.project.accommodation_service.core.domain.dto.response.PriceCalculationResponse;
import huy.project.accommodation_service.core.domain.entity.PricingRuleEntity;
import huy.project.accommodation_service.core.domain.entity.UnitPriceCalendarEntity;

import java.time.LocalDate;
import java.util.List;

public interface IPricingService {
    List<UnitPriceCalendarEntity> getUnitPricingCalendars(Long unitId, LocalDate startDate, LocalDate endDate);
    List<UnitPriceCalendarEntity> updateBasePrice(Long userId, Long unitId, UpdateBasePriceRequest request);
    PricingRuleEntity createPricingRule(Long userId, CreatePricingRuleDto req);
    List<PricingRuleEntity> getPricingRules(PricingRuleParams params);
    void deletePricingRule(Long userId, Long pricingRuleId);
    PriceCalculationResponse calculatePrice(PriceCalculationRequest request);
}
