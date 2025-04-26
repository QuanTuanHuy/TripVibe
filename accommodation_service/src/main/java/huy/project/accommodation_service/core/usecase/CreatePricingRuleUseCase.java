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
    public PricingRuleEntity createPricingRule(Long userId, CreatePricingRuleDto req) {
        // validate input
        if (req.getUnitId() == null && req.getAccommodationId() == null) {
            log.error("unit id or accommodation id must be provided");
            throw new AppException(ErrorCode.INVALID_UNIT_OR_ACCOMMODATION_ID);
        }

        if(req.getStartDate().isAfter(req.getEndDate())) {
            log.error("start date must be before end date");
            throw new AppException(ErrorCode.INVALID_DATE);
        }

        // validate owner
        if (req.getAccommodationId() != null) {
            if (!accValidation.accommodationExistToHost(userId, req.getAccommodationId())) {
                log.error("user {} is not owner of accommodation {}", userId, req.getAccommodationId());
                throw new AppException(ErrorCode.FORBIDDEN_CREATE_PRICING_RULE);
            }
        } else {
            var isOwnerOfUnit = accValidation.isOwnerOfUnit(userId, req.getUnitId());
            if (!isOwnerOfUnit.getFirst()) {
                log.error("user {} is not owner of unit {}", userId, req.getUnitId());
                throw new AppException(ErrorCode.FORBIDDEN_CREATE_PRICING_RULE);
            }
        }

        // create pricing rule
        var pricingRule = PricingRuleMapper.INSTANCE.toEntity(req);
        pricingRule.setIsActive(true);

        return pricingRulePort.save(pricingRule);
    }
}
