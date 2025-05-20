package huy.project.inventory_service.core.port;

import huy.project.inventory_service.core.domain.entity.Unit;

import java.util.List;

public interface IUnitPort {
    Unit getUnitById(Long id);
    
    List<Unit> getUnitsByAccommodationId(Long accommodationId);

    Unit save(Unit unit);
    
    List<Unit> saveAll(List<Unit> units);
    
    void deleteUnitById(Long id);
}
