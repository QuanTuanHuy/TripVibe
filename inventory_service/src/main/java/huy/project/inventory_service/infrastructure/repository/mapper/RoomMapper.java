package huy.project.inventory_service.infrastructure.repository.mapper;

import huy.project.inventory_service.core.domain.entity.Room;
import huy.project.inventory_service.infrastructure.repository.model.RoomModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public abstract class RoomMapper {
    
    public static final RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);
    
    public abstract Room toEntity(RoomModel model);

    public abstract RoomModel toModel(Room entity);
    
    public abstract List<Room> toEntityList(List<RoomModel> models);
    
    public abstract List<RoomModel> toModelList(List<Room> entities);
}
