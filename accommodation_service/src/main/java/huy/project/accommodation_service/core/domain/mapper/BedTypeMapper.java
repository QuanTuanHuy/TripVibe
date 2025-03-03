package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.CreateBedTypeDto;
import huy.project.accommodation_service.core.domain.entity.BedTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class BedTypeMapper {
    public static final BedTypeMapper INSTANCE = Mappers.getMapper(BedTypeMapper.class);

    public abstract BedTypeEntity toEntity(CreateBedTypeDto req);
}
