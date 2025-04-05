package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.BedEntity;
import huy.project.accommodation_service.infrastructure.repository.model.BedModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class BedMapper {
    public static final BedMapper INSTANCE = Mappers.getMapper(BedMapper.class);

    public abstract BedEntity toEntity(BedModel model);
    public abstract BedModel toModel(BedEntity entity);
    public abstract List<BedEntity> toListEntity(List<BedModel> models);
    public abstract List<BedModel> toListModel(List<BedEntity> entities);
}
