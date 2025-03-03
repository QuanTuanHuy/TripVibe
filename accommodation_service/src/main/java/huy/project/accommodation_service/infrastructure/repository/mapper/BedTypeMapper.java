package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.BedTypeEntity;
import huy.project.accommodation_service.infrastructure.repository.model.BedTypeModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class BedTypeMapper {
    public static final BedTypeMapper INSTANCE = Mappers.getMapper(BedTypeMapper.class);

    public abstract BedTypeEntity toEntity(BedTypeModel model);

    public abstract BedTypeModel toModel(BedTypeEntity entity);

    public abstract List<BedTypeEntity> toListEntity(List<BedTypeModel> models);
}
