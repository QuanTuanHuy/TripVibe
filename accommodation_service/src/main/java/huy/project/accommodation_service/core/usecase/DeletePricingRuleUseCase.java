package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IPricingRulePort;
import huy.project.accommodation_service.core.validation.AccommodationValidation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeletePricingRuleUseCase {
    IPricingRulePort pricingRulePort;
    AccommodationValidation accValidation;

    PriceCalculationCacheUseCase priceCalculationCacheUseCase;
    GetUnitUseCase getUnitUseCase;
    DynamicPricingUseCase dynamicPricingUseCase;

    @Transactional(rollbackFor = Exception.class)
    public void deletePricingRule(Long userId, Long pricingRuleId) {
        var pricingRule = pricingRulePort.getPricingRuleById(pricingRuleId);
        if (pricingRule == null) {
            log.error("pricing rule {} not found", pricingRuleId);
            throw new AppException(ErrorCode.PRICING_RULE_NOT_FOUND);
        }

        // validate owner of pricing rule
        if (pricingRule.getAccommodationId() != null) {
            if (!accValidation.accommodationExistToHost(userId, pricingRule.getAccommodationId())) {
                log.error("user {} is not owner of accommodation {}", userId, pricingRule.getAccommodationId());
                throw new AppException(ErrorCode.FORBIDDEN_DELETE_PRICING_RULE);
            }
        } else if (pricingRule.getUnitId() != null) {
            var isOwnerOfUnit = accValidation.isOwnerOfUnit(userId, pricingRule.getUnitId());
            if (!isOwnerOfUnit.getFirst()) {
                log.error("user {} is not owner of unit {}", userId, pricingRule.getUnitId());
                throw new AppException(ErrorCode.FORBIDDEN_DELETE_PRICING_RULE);
            }
        }

        // apply rule amd invalidate cache
        if (pricingRule.getUnitId() != null) {
            dynamicPricingUseCase.applyPricingRules(pricingRule.getUnitId(), pricingRule.getStartDate(), pricingRule.getEndDate());
        } else {
            var units = getUnitUseCase.getUnitsByAccommodationId(pricingRule.getAccommodationId());
            for (var unit : units) {
                // apply rule
                dynamicPricingUseCase.applyPricingRules(unit.getId(), pricingRule.getStartDate(), pricingRule.getEndDate());
                // invalidate cache
                priceCalculationCacheUseCase.invalidatePriceCache(unit.getId());
            }
        }

        pricingRulePort.deletePricingRule(pricingRuleId);
    }
}
