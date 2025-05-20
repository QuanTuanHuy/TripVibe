package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.port.IRoomPort;
import huy.project.inventory_service.core.port.IUnitPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
