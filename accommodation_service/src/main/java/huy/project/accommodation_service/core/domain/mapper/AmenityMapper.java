package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.entity.CreateAmenityRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AmenityMapper {
    public static final AmenityMapper INSTANCE = Mappers.getMapper(AmenityMapper.class);

    public abstract AmenityEntity toEntity(CreateAmenityRequestDto req);
}
