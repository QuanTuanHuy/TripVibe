package huy.project.inventory_service.infrastructure.repository.adapter;

import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.port.IUnitPort;
import org.springframework.stereotype.Component;

@Component
public class UnitAdapter implements IUnitPort {
    @Override
    public Unit getUnitById(Long id) {
        return null;
    }

    @Override
    public Unit save(Unit unit) {
        return null;
    }
}
