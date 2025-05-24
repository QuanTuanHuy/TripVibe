package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.entity.Room;
import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.port.IRoomPort;
import huy.project.inventory_service.core.port.IUnitPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUnitUseCase {
    private final IUnitPort unitPort;
    private final IRoomPort roomPort;

    public Unit getUnitById(Long id) {
        Unit unit = unitPort.getUnitById(id);
        if (unit == null) {
            return null;
        }
        unit.setRooms(roomPort.getRoomsByUnitId(id));
        return unit;
    }

    public List<Unit> getUnitsByAccommodationId(Long accId) {
        return getUnitsByAccommodationId(accId, false);
    }

    public List<Unit> getUnitsByAccommodationId(Long accId, boolean includeRooms) {
        List<Unit> units = unitPort.getUnitsByAccommodationId(accId);
        if (CollectionUtils.isEmpty(units)) {
            return List.of();
        }

        List<Long> unitIds = units.stream().map(Unit::getId).toList();

        if (includeRooms) {
            List<Room> rooms = roomPort.getRoomsByUnitIds(unitIds);
            var roomMap = rooms.stream()
                    .collect(Collectors.groupingBy(Room::getUnitId));
            units.forEach(unit -> unit.setRooms(roomMap.get(unit.getId())));
        }

        return units;
    }
}
