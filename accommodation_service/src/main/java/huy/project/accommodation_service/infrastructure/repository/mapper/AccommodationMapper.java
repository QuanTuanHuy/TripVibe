package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.infrastructure.repository.model.AccommodationModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class AccommodationMapper {
    public static final AccommodationMapper INSTANCE = Mappers.getMapper(AccommodationMapper.class);

    public abstract AccommodationEntity toEntity(AccommodationModel model);

    public abstract AccommodationModel toModel(AccommodationEntity entity);

    public abstract List<AccommodationEntity> toListEntity(List<AccommodationModel> models);
}
