package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.UnitAmenityModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUnitAmenityRepository extends IBaseRepository<UnitAmenityModel> {
    List<UnitAmenityModel> findByUnitIdIn(List<Long> unitIds);
}
