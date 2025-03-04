package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.CreateBedroomDto;
import huy.project.accommodation_service.core.domain.entity.BedroomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class BedroomMapper {
    public static final BedroomMapper INSTANCE = Mappers.getMapper(BedroomMapper.class);

    public BedroomEntity toEntity(Long unitId, CreateBedroomDto req) {
        return BedroomEntity.builder()
                .unitId(unitId)
                .quantity(req.getQuantity())
                .build();
    }
}
