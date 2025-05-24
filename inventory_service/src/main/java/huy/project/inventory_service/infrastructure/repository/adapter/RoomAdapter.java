package huy.project.inventory_service.infrastructure.repository.adapter;

import huy.project.inventory_service.core.domain.entity.Room;
import huy.project.inventory_service.core.port.IRoomPort;
import huy.project.inventory_service.infrastructure.repository.IRoomRepository;
import huy.project.inventory_service.infrastructure.repository.mapper.RoomMapper;
import huy.project.inventory_service.infrastructure.repository.model.RoomModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomAdapter implements IRoomPort {

    private final IRoomRepository roomRepository;

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .map(RoomMapper.INSTANCE::toEntity)
                .orElse(null);
    }

    @Override
    public List<Room> getRoomsByUnitId(Long unitId) {
        List<RoomModel> models = roomRepository.findByUnitId(unitId);
        return RoomMapper.INSTANCE.toEntityList(models);
    }

    @Override
    public List<Room> getRoomsByUnitIds(List<Long> unitIds) {
        return RoomMapper.INSTANCE.toEntityList(roomRepository.findByUnitIdIn(unitIds));
    }

    @Override
    public Room save(Room room) {
        RoomModel model = RoomMapper.INSTANCE.toModel(room);
        RoomModel savedModel = roomRepository.save(model);
        return RoomMapper.INSTANCE.toEntity(savedModel);
    }

    @Override
    public List<Room> saveAll(List<Room> rooms) {
        List<RoomModel> models = RoomMapper.INSTANCE.toModelList(rooms);
        List<RoomModel> savedModels = roomRepository.saveAll(models);
        return RoomMapper.INSTANCE.toEntityList(savedModels);
    }

    @Override
    public void deleteRoomsByIds(List<Long> roomIds) {
        roomRepository.deleteAllById(roomIds);
    }
}
