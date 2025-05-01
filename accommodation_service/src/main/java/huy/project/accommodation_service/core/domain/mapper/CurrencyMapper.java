package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.CreateCurrencyDto;
import huy.project.accommodation_service.core.domain.entity.CurrencyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class CurrencyMapper {
    public static final CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    public abstract CurrencyEntity toEntity(CreateCurrencyDto req);
}
