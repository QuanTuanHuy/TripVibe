package huy.project.profile_service.infrastructure.repository.mapper;

import huy.project.profile_service.core.domain.entity.TouristEntity;
import huy.project.profile_service.infrastructure.repository.model.TouristModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class TouristMapper {
    public static final TouristMapper INSTANCE = Mappers.getMapper(TouristMapper.class);

    public abstract TouristEntity toEntity(TouristModel model);
    public abstract TouristModel toModel(TouristEntity entity);
}
