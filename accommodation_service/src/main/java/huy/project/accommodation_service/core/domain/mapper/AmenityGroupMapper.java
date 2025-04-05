package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.constant.AmenityGroupType;
import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AmenityGroupMapper {
    public static final AmenityGroupMapper INSTANCE = Mappers.getMapper(AmenityGroupMapper.class);

    @Mapping(target = "type", ignore = true)
    public abstract AmenityGroupEntity toEntity(CreateAmenityGroupRequestDto req);

    public AmenityGroupEntity toEntity(AmenityGroupEntity existedAmenityGroup, UpdateAmenityGroupRequestDto req) {
        existedAmenityGroup.setName(req.getName());
        existedAmenityGroup.setType(AmenityGroupType.of(req.getType()));
        existedAmenityGroup.setDescription(req.getDescription());
        return existedAmenityGroup;
    }
}
