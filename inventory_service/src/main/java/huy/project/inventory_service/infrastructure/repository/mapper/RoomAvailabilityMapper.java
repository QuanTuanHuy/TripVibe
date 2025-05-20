package huy.project.inventory_service.infrastructure.repository.mapper;

import huy.project.inventory_service.core.domain.entity.RoomAvailability;
import huy.project.inventory_service.infrastructure.repository.model.RoomAvailabilityModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class RoomAvailabilityMapper {
    
    public static final RoomAvailabilityMapper INSTANCE = Mappers.getMapper(RoomAvailabilityMapper.class);

    public abstract RoomAvailability toEntity(RoomAvailabilityModel model);

    public abstract RoomAvailabilityModel toModel(RoomAvailability entity);
    
    public abstract List<RoomAvailability> toEntityList(List<RoomAvailabilityModel> models);
    
    public abstract List<RoomAvailabilityModel> toModelList(List<RoomAvailability> entities);
}
