package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.CreateUnitDto;
import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class UnitMapper {
    public static final UnitMapper INSTANCE = Mappers.getMapper(UnitMapper.class);

    public UnitEntity toEntity(Long accommodationId, CreateUnitDto req) {
        return UnitEntity.builder()
                .accommodationId(accommodationId)
                .unitNameId(req.getUnitNameId())
                .description(req.getDescription())
                .pricePerNight(req.getPricePerNight())
                .maxGuest(req.getMaxGuest())
                .useSharedBathroom(req.getUseSharedBathroom())
                .build();
    }
}
