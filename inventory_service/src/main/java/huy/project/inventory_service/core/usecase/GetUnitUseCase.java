package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.port.IUnitPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUnitUseCase {
    private final IUnitPort unitPort;

    public Unit getUnitById(Long id) {
        return unitPort.getUnitById(id);
    }
}
