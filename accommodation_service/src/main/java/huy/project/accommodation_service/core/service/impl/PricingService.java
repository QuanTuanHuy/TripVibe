package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.CreatePricingRuleDto;
import huy.project.accommodation_service.core.domain.dto.request.PriceCalculationRequest;
import huy.project.accommodation_service.core.domain.dto.request.PricingRuleParams;
import huy.project.accommodation_service.core.domain.dto.request.UpdateBasePriceRequest;
import huy.project.accommodation_service.core.domain.dto.response.PriceCalculationResponse;
import huy.project.accommodation_service.core.domain.entity.PricingRuleEntity;
import huy.project.accommodation_service.core.domain.entity.UnitPriceCalendarEntity;
import huy.project.accommodation_service.core.service.IPricingService;
import huy.project.accommodation_service.core.usecase.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PricingService implements IPricingService {
    CreatePricingRuleUseCase createPricingRuleUseCase;
    DeletePricingRuleUseCase deletePricingRuleUseCase;
    GetPricingRuleUseCase getPricingRuleUseCase;
    GetUnitPricingCalendarUseCase getUnitPricingCalendarUseCase;
    DynamicPricingUseCase dynamicPricingUseCase;

    @Override
    public List<UnitPriceCalendarEntity> getUnitPricingCalendars(Long unitId, LocalDate startDate, LocalDate endDate) {
        return getUnitPricingCalendarUseCase.getUnitPricingCalendars(unitId, startDate, endDate);
    }

    @Override
    public List<UnitPriceCalendarEntity> updateBasePrice(Long userId, Long unitId, UpdateBasePriceRequest request) {
        return dynamicPricingUseCase.updateBasePrice(userId, unitId, request.getStartDate(), request.getEndDate(), request.getBasePrice(), "API_UPDATE");
    }

    @Override
    public PricingRuleEntity createPricingRule(Long userId, CreatePricingRuleDto req) {
        return createPricingRuleUseCase.createPricingRule(userId, req);
    }

    @Override
    public List<PricingRuleEntity> getPricingRules(PricingRuleParams params) {
        return getPricingRuleUseCase.getPricingRules(params);
    }

    @Override
    public void deletePricingRule(Long userId, Long pricingRuleId) {
        deletePricingRuleUseCase.deletePricingRule(userId, pricingRuleId);
    }

    @Override
    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request) {
        return dynamicPricingUseCase.calculatePrice(request);
    }

}
