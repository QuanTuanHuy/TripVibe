package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;
import huy.project.accommodation_service.infrastructure.repository.model.PriceTypeModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class PriceTypeMapper {
    public static final PriceTypeMapper INSTANCE = Mappers.getMapper(PriceTypeMapper.class);

    public abstract PriceTypeEntity toEntity(PriceTypeModel model);
    public abstract List<PriceTypeEntity> toListEntity(List<PriceTypeModel> models);
}
