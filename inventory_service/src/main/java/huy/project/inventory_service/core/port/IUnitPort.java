package huy.project.inventory_service.core.port;

import huy.project.inventory_service.core.domain.entity.Unit;

public interface IUnitPort {
    Unit getUnitById(Long id);

    Unit save(Unit unit);
}
