package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.UnitAmenityEntity;
import huy.project.accommodation_service.infrastructure.repository.model.UnitAmenityModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class UnitAmenityMapper {
    public static final UnitAmenityMapper INSTANCE = Mappers.getMapper(UnitAmenityMapper.class);

    public abstract UnitAmenityEntity toEntity(UnitAmenityModel model);
    public abstract UnitAmenityModel toModel(UnitAmenityEntity entity);
    public abstract List<UnitAmenityModel> toListModel(List<UnitAmenityEntity> entities);
    public abstract List<UnitAmenityEntity> toListEntity(List<UnitAmenityModel> models);
}
