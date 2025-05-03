package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.CreatePriceTypeDto;
import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class PriceTypeMapper {
    public static final PriceTypeMapper INSTANCE = Mappers.getMapper(PriceTypeMapper.class);

    public abstract PriceTypeEntity toEntity(CreatePriceTypeDto req);
}
