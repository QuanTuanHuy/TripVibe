package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.UnitNameEntity;
import huy.project.accommodation_service.infrastructure.repository.model.UnitNameModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class UnitNameMapper {
    public static final UnitNameMapper INSTANCE = Mappers.getMapper(UnitNameMapper.class);

    public abstract UnitNameEntity toEntity(UnitNameModel unitName);

    public abstract UnitNameModel toModel(UnitNameEntity unitName);

    public abstract List<UnitNameEntity> toListEntity(List<UnitNameModel> unitNames);
}
