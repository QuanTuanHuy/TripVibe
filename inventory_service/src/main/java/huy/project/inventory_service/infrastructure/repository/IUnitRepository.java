package huy.project.inventory_service.infrastructure.repository;

import huy.project.inventory_service.infrastructure.repository.model.UnitModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUnitRepository extends IBaseRepository<UnitModel> {
    List<UnitModel> findByAccommodationId(Long accommodationId);
}
