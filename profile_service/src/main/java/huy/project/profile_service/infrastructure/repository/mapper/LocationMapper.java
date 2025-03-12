package huy.project.profile_service.infrastructure.repository.mapper;

import huy.project.profile_service.core.domain.entity.LocationEntity;
import huy.project.profile_service.infrastructure.repository.model.LocationModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class LocationMapper {
    public static final LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    public abstract LocationEntity toEntity(LocationModel model);
    public abstract LocationModel toModel(LocationEntity entity);
}
