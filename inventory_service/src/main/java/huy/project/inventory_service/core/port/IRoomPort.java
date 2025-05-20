package huy.project.inventory_service.core.port;

import huy.project.inventory_service.core.domain.entity.Room;

import java.util.List;

public interface IRoomPort {
    List<Room> saveAll(List<Room> rooms);

    List<Room> getRoomsByUnitId(Long unitId);

    Room getRoomById(Long id);
    
    Room save(Room room);

    void deleteRoomsByIds(List<Long> ids);
}
