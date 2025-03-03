package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationTypeDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class AccommodationTypeMapper {
    public static final AccommodationTypeMapper INSTANCE = Mappers.getMapper(AccommodationTypeMapper.class);

    public abstract AccommodationTypeEntity toEntity(CreateAccommodationTypeDto req);
}
