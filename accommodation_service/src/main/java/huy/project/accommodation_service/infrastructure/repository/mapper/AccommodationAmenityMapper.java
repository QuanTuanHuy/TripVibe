package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.AccommodationAmenityEntity;
import huy.project.accommodation_service.infrastructure.repository.model.AccommodationAmenityModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class AccommodationAmenityMapper {
    public static final AccommodationAmenityMapper INSTANCE = Mappers.getMapper(AccommodationAmenityMapper.class);

    public abstract AccommodationAmenityEntity toEntity(AccommodationAmenityModel model);
    public abstract AccommodationAmenityModel toModel(AccommodationAmenityEntity entity);
    public abstract List<AccommodationAmenityModel> toListModel(List<AccommodationAmenityEntity> entities);
    public abstract List<AccommodationAmenityEntity> toListEntity(List<AccommodationAmenityModel> models);
}
