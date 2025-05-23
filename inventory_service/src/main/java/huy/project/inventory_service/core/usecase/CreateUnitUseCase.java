package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.dto.request.SyncUnitDto;
import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.port.IUnitPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateUnitUseCase {
    private final IUnitPort unitPort;
    private final CreateRoomUseCase createRoomUseCase;

    @Transactional(rollbackFor = Exception.class)
    public Unit createUnit(SyncUnitDto request) {
        var savedUnit = unitPort.save(Unit.builder()
                        .id(request.getUnitId())
                        .accommodationId(request.getAccommodationId())
                        .unitNameId(request.getUnitNameId())
                        .unitName(request.getUnitName())
                        .basePrice(request.getBasePrice())
                        .quantity(request.getQuantity())
                .build());

        createRoomUseCase.createRoomsForUnit(savedUnit, request.getQuantity());

        return savedUnit;
    }
}
