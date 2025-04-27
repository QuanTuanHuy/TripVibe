package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.PricingRuleParams;
import huy.project.accommodation_service.core.domain.entity.PricingRuleEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IPricingRulePort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GetPricingRuleUseCase {
    IPricingRulePort pricingRulePort;
    GetUnitUseCase getUnitUseCase;

    public List<PricingRuleEntity> getPricingRules(PricingRuleParams params) {
        if (params.getUnitId() == null && params.getAccommodationId() == null) {
            log.error("unit id or accommodation id must be provided");
            throw new AppException(ErrorCode.INVALID_UNIT_OR_ACCOMMODATION_ID);
        }

        if (params.getUnitId() != null) {
            var unit = getUnitUseCase.getUnitById(params.getUnitId());
            params.setAccommodationId(unit.getAccommodationId());
        }

        return pricingRulePort.getPricingRules(params);
    }
}
