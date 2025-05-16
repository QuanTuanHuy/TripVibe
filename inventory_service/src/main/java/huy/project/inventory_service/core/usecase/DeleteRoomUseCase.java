package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.entity.Room;
import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.port.IRoomPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeleteRoomUseCase {
    private final IRoomPort roomPort;

    @Transactional(rollbackFor = Exception.class)
    public void deleteRoomsFromUnit(Unit unit, int deleteRoomCount) {
        // cần kiểm tra phòng nào không có booking trước

        List<Room> rooms = roomPort.getRoomsByUnitId(unit.getId());
        if (rooms.size() < deleteRoomCount) {
            throw new IllegalArgumentException("Not enough rooms to delete");
        }

        List<Long> roomIdsToDelete = rooms.stream()
                .map(Room::getId)
                .limit(deleteRoomCount)
                .toList();
        roomPort.deleteRoomsByIds(roomIdsToDelete);
    }
}
