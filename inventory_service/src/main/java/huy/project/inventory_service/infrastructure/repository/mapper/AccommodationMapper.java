package huy.project.inventory_service.infrastructure.repository.mapper;

import huy.project.inventory_service.core.domain.entity.Accommodation;
import huy.project.inventory_service.infrastructure.repository.model.AccommodationModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class AccommodationMapper {
    public static final AccommodationMapper INSTANCE = Mappers.getMapper(AccommodationMapper.class);

    public abstract Accommodation toEntity(AccommodationModel model);
    
    public abstract AccommodationModel toModel(Accommodation entity);
    
    public abstract List<Accommodation> toEntityList(List<AccommodationModel> models);
    
    public abstract List<AccommodationModel> toModelList(List<Accommodation> domains);
}
