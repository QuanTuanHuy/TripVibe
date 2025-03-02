package huy.project.accommodation_service.core.domain.mapper;

import huy.project.accommodation_service.core.domain.dto.request.CreateUnitNameDto;
import huy.project.accommodation_service.core.domain.entity.UnitNameEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class UnitNameMapper {
    public static final UnitNameMapper INSTANCE = Mappers.getMapper(UnitNameMapper.class);

    public abstract UnitNameEntity toEntity(CreateUnitNameDto req);
}
