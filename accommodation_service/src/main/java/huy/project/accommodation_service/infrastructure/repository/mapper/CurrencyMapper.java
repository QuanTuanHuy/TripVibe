package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.CurrencyEntity;
import huy.project.accommodation_service.infrastructure.repository.model.CurrencyModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class CurrencyMapper {
    public static final CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    public abstract CurrencyEntity toEntity(CurrencyModel model);

    public abstract CurrencyModel toModel(CurrencyEntity entity);

    public abstract List<CurrencyEntity> toListEntity(List<CurrencyModel> models);
}
