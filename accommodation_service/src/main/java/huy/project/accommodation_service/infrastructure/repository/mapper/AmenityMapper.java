package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.infrastructure.repository.model.AmenityModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class AmenityMapper {
    public static final AmenityMapper INSTANCE = Mappers.getMapper(AmenityMapper.class);

    public abstract AmenityEntity toEntity(AmenityModel amenity);

    public abstract AmenityModel toModel(AmenityEntity amenity);

    public abstract List<AmenityEntity> toListAmenity(List<AmenityModel> amenities);
}
