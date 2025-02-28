package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.UpdateAmenityRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AmenityMapper {
    public static final AmenityMapper INSTANCE = Mappers.getMapper(AmenityMapper.class);

    public abstract AmenityEntity toEntity(CreateAmenityRequestDto req);

    public AmenityEntity toEntity(AmenityEntity existedAmenity, UpdateAmenityRequestDto req) {
        existedAmenity.setName(req.getName());
        existedAmenity.setDescription(req.getDescription());
        existedAmenity.setGroupId(req.getGroupId());
        return existedAmenity;
    }
}
