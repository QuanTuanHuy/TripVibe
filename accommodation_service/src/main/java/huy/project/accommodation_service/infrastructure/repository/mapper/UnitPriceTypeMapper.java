package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.UnitPriceTypeEntity;
import huy.project.accommodation_service.infrastructure.repository.model.UnitPriceTypeModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class UnitPriceTypeMapper {
    public static final UnitPriceTypeMapper INSTANCE = Mappers.getMapper(UnitPriceTypeMapper.class);

    public abstract UnitPriceTypeModel toModel(UnitPriceTypeEntity entity);
    public abstract UnitPriceTypeEntity toEntity(UnitPriceTypeModel model);
    public abstract List<UnitPriceTypeEntity> toListEntity(List<UnitPriceTypeModel> models);
    public abstract List<UnitPriceTypeModel> toListModel(List<UnitPriceTypeEntity> entities);
}
