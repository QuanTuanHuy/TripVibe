package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.UnitPriceGroupEntity;
import huy.project.accommodation_service.infrastructure.repository.model.UnitPriceGroupModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class UnitPriceGroupMapper {
    public static final UnitPriceGroupMapper INSTANCE = Mappers.getMapper(UnitPriceGroupMapper.class);

    public abstract UnitPriceGroupModel toModel(UnitPriceGroupEntity entity);
    public abstract UnitPriceGroupEntity toEntity(UnitPriceGroupModel model);
    public abstract List<UnitPriceGroupEntity> toListEntity(List<UnitPriceGroupModel> models);
    public abstract List<UnitPriceGroupModel> toListModel(List<UnitPriceGroupEntity> entities);
}
