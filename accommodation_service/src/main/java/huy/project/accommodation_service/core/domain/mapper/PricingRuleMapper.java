package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.CreatePricingRuleDto;
import huy.project.accommodation_service.core.domain.entity.PricingRuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class PricingRuleMapper {
    public static final PricingRuleMapper INSTANCE = Mappers.getMapper(PricingRuleMapper.class);

    @Mapping(target = "additionalParams", ignore = true)
    public abstract PricingRuleEntity toEntity(CreatePricingRuleDto req);
}
