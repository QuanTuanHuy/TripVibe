package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.dto.request.SyncUnitDto;
import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.domain.exception.NotFoundException;
import huy.project.inventory_service.core.port.IUnitPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUnitUseCase {
    private final CreateRoomUseCase createRoomUseCase;
    private final DeleteRoomUseCase deleteRoomUseCase;
    private final IUnitPort unitPort;

    @Transactional(rollbackFor = Exception.class)
    public Unit updateUnit(SyncUnitDto request) {
        var unit = unitPort.getUnitById(request.getUnitId());
        if (unit == null) {
            throw new NotFoundException("Unit not found, id: " + request.getUnitId());
        }

        if (unit.getQuantity() < request.getQuantity()) {
            int newRoomCount = request.getQuantity() - unit.getQuantity();
            createRoomUseCase.createRoomsForUnit(unit, newRoomCount);
        } else {
            int roomCountToDelete = unit.getQuantity() - request.getQuantity();
            if (roomCountToDelete > 0) {
                deleteRoomUseCase.deleteRoomsFromUnit(unit, roomCountToDelete);
            }
        }

        unit.setQuantity(request.getQuantity());
        return unitPort.save(unit);
    }
}
