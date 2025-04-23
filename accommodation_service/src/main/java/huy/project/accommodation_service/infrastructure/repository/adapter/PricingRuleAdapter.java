package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.entity.PricingRuleEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IPricingRulePort;
import huy.project.accommodation_service.infrastructure.repository.IPricingRuleRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.PricingRuleMapper;
import huy.project.accommodation_service.infrastructure.repository.model.PricingRuleModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PricingRuleAdapter implements IPricingRulePort {
    IPricingRuleRepository pricingRuleRepository;

    @Override
    public PricingRuleEntity save(PricingRuleEntity pricingRule) {
        try {
            PricingRuleModel model = PricingRuleMapper.INSTANCE.toModel(pricingRule);
            return PricingRuleMapper.INSTANCE.toEntity(pricingRuleRepository.save(model));
        } catch (Exception e) {
            log.error("Error when saving pricing rule: {}", e.getMessage());
            throw new AppException(ErrorCode.SAVE_PRICING_RULE_FAILED);
        }
    }

    @Override
    public PricingRuleEntity getPricingRuleById(Long id) {
        return PricingRuleMapper.INSTANCE.toEntity(pricingRuleRepository.findById(id).orElse(null));
    }

    @Override
    public void deletePricingRule(Long id) {
        try {
            pricingRuleRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error when deleting pricing rule: {}", e.getMessage());
            throw new AppException(ErrorCode.DELETE_PRICING_RULE_FAILED);
        }
    }
}
