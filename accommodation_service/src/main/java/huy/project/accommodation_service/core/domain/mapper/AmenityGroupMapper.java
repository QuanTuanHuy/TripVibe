package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AmenityGroupMapper {
    public static final AmenityGroupMapper INSTANCE = Mappers.getMapper(AmenityGroupMapper.class);

    public abstract AmenityGroupEntity toEntity(CreateAmenityGroupRequestDto req);
}
