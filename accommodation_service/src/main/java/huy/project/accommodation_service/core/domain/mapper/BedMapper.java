package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.CreateBedDto;
import huy.project.accommodation_service.core.domain.entity.BedEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class BedMapper {
    public static final BedMapper INSTANCE = Mappers.getMapper(BedMapper.class);

    public BedEntity toEntity(Long bedroomId, CreateBedDto req) {
        return BedEntity.builder()
                .bedroomId(bedroomId)
                .bedTypeId(req.getBedTypeId())
                .quantity(req.getQuantity())
                .build();
    }
}
