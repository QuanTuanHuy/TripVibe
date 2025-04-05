package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import huy.project.accommodation_service.infrastructure.repository.model.UnitModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class UnitMapper {
    public static final UnitMapper INSTANCE = Mappers.getMapper(UnitMapper.class);

    public abstract UnitEntity toEntity(UnitModel model);
    public abstract UnitModel toModel(UnitEntity entity);
    public abstract List<UnitEntity> toListEntity(List<UnitModel> models);
}
