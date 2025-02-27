package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.infrastructure.repository.model.AmenityGroupModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AmenityGroupMapper {
    public static final AmenityGroupMapper INSTANCE = Mappers.getMapper(AmenityGroupMapper.class);

    public abstract AmenityGroupEntity toEntity(AmenityGroupModel amenity);

    public abstract AmenityGroupModel toModel(AmenityGroupEntity amenity);
}
