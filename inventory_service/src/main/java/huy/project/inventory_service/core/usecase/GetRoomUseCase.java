package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.entity.Room;
import huy.project.inventory_service.core.domain.exception.NotFoundException;
import huy.project.inventory_service.core.port.IRoomPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetRoomUseCase {
    private final IRoomPort roomPort;

    public List<Room> getRoomsByUnitId(Long unitId) {
        return roomPort.getRoomsByUnitId(unitId);
    }

    public Room getRoomById(Long roomId) {
        Room room = roomPort.getRoomById(roomId);
        if (room == null) {
            throw new NotFoundException("Room not found with id: " + roomId);
        }
        return room;
    }
}
