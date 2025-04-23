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

    @Transactional(rollbackFor = Exception.class)
    public void deletePricingRule(Long userId, Long unitId, Long pricingRuleId) {
        // validate owner
        var isOwnerOfUnit = accValidation.isOwnerOfUnit(userId, unitId);
        if (!isOwnerOfUnit.getFirst()) {
            log.error("user {} is not owner of unit {}", userId, unitId);
            throw new AppException(ErrorCode.FORBIDDEN_DELETE_PRICING_RULE);
        }

        var pricingRule = pricingRulePort.getPricingRuleById(pricingRuleId);
        if (pricingRule == null) {
            log.error("pricing rule {} not found", pricingRuleId);
            throw new AppException(ErrorCode.PRICING_RULE_NOT_FOUND);
        }

        if (!pricingRule.getUnitId().equals(unitId)) {
            log.error("pricing rule {} not belong to unit {}", pricingRuleId, unitId);
            throw new AppException(ErrorCode.PRICING_RULE_NOT_FOUND);
        }

        pricingRulePort.deletePricingRule(pricingRuleId);
    }
}
