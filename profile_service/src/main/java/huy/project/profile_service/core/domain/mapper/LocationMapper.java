package huy.project.profile_service.core.domain.mapper;

import huy.project.profile_service.core.domain.dto.request.UpdateLocationDto;
import huy.project.profile_service.core.domain.entity.LocationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class LocationMapper {
    public static final LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    public abstract LocationEntity toEntity(UpdateLocationDto req);
}
