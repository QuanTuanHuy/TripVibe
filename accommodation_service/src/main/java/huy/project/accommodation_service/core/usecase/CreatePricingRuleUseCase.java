package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreatePricingRuleDto;
import huy.project.accommodation_service.core.domain.entity.PricingRuleEntity;
import huy.project.accommodation_service.core.domain.mapper.PricingRuleMapper;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CreatePricingRuleUseCase {
    IPricingRulePort pricingRulePort;
    AccommodationValidation accValidation;

    @Transactional(rollbackFor = Exception.class)
    public PricingRuleEntity createPricingRule(Long userId, Long unitId, CreatePricingRuleDto req) {
        // validate owner
        var isOwnerOfUnit = accValidation.isOwnerOfUnit(userId, unitId);
        if (!isOwnerOfUnit.getFirst()) {
            log.error("user {} is not owner of unit {}", userId, unitId);
            throw new AppException(ErrorCode.FORBIDDEN_CREATE_PRICING_RULE);
        }

        if(req.getStartDate().isAfter(req.getEndDate())) {
            log.error("start date must be before end date");
            throw new AppException(ErrorCode.INVALID_DATE);
        }

        // create pricing rule
        var pricingRule = PricingRuleMapper.INSTANCE.toEntity(req);
        pricingRule.setUnitId(unitId);
        pricingRule.setIsActive(true);

        return pricingRulePort.save(pricingRule);
    }
}
