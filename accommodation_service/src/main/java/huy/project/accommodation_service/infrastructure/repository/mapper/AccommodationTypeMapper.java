package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;
import huy.project.accommodation_service.infrastructure.repository.model.AccommodationTypeModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AccommodationTypeMapper {
    public static final AccommodationTypeMapper INSTANCE = Mappers.getMapper(AccommodationTypeMapper.class);

    public abstract AccommodationTypeEntity toEntity(AccommodationTypeModel model);
    public abstract AccommodationTypeModel toModel(AccommodationTypeEntity entity);
}
