package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.PricingRuleEntity;
import huy.project.accommodation_service.infrastructure.repository.model.PricingRuleModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class PricingRuleMapper {
    public static final PricingRuleMapper INSTANCE = Mappers.getMapper(PricingRuleMapper.class);

    public abstract PricingRuleEntity toEntity(PricingRuleModel pricingRuleModel);

    public abstract PricingRuleModel toModel(PricingRuleEntity pricingRuleEntity);
}
