package huy.project.inventory_service.infrastructure.repository.adapter;

import huy.project.inventory_service.core.domain.entity.Room;
import huy.project.inventory_service.core.port.IRoomPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoomAdapter implements IRoomPort {
    @Override
    public List<Room> saveAll(List<Room> rooms) {
        return List.of();
    }

    @Override
    public List<Room> getRoomsByUnitId(Long unitId) {
        return List.of();
    }

    @Override
    public Room getRoomById(Long id) {
        return null;
    }

    @Override
    public void deleteRoomsByIds(List<Long> ids) {

    }
}
