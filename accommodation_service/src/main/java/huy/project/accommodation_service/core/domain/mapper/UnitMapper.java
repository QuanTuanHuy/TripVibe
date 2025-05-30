package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.CreateUnitDtoV2;
import huy.project.accommodation_service.core.domain.dto.response.UnitDto;
import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class UnitMapper {
    public static final UnitMapper INSTANCE = Mappers.getMapper(UnitMapper.class);

    public UnitEntity toEntity(Long accommodationId, CreateUnitDtoV2 req) {
        return UnitEntity.builder()
                .accommodationId(accommodationId)
                .unitNameId(req.getUnitNameId())
                .description(req.getDescription())
                .pricePerNight(req.getPricePerNight())
                .maxAdults(req.getMaxAdults())
                .maxChildren(req.getMaxChildren())
                .useSharedBathroom(req.getUseSharedBathroom())
                .quantity(req.getQuantity())
                .build();
    }

    public UnitDto toDto(UnitEntity unit) {
        return UnitDto.builder()
                .id(unit.getId())
                .name(unit.getUnitName().getName())
                .accommodationId(unit.getAccommodationId())
                .build();
    }
}
