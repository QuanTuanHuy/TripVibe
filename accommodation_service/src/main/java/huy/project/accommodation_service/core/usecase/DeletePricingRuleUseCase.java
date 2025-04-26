package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IPricingRulePort;
import huy.project.accommodation_service.core.validation.AccommodationValidation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeletePricingRuleUseCase {
    IPricingRulePort pricingRulePort;
    AccommodationValidation accValidation;
    PriceCalculationCacheUseCase priceCalculationCacheUseCase;

    GetUnitUseCase getUnitUseCase;

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

        // Invalidate cache
        if (pricingRule.getUnitId() != null) {
            var success = priceCalculationCacheUseCase.invalidatePriceCache(pricingRule.getUnitId());

            if (!success)
                throw new AppException(ErrorCode.DELETE_PRICING_RULE_FAILED);
        } else {
            List<Long> unitIds = getUnitUseCase.getUnitsByAccommodationId(pricingRule.getAccommodationId())
                    .stream().map(UnitEntity::getId).toList();

            for (var unitId : unitIds) {
                var success = priceCalculationCacheUseCase.invalidatePriceCache(unitId);
                if (!success) {
                    log.error("failed to invalidate cache for unit {}", unitId);
                    throw new AppException(ErrorCode.DELETE_PRICING_RULE_FAILED);
                }
            }
        }

        pricingRulePort.deletePricingRule(pricingRuleId);
    }
}
