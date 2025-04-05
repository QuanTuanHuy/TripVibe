package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.UnitPriceTypeModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUnitPriceTypeRepository extends IBaseRepository<UnitPriceTypeModel> {
    List<UnitPriceTypeModel> findByUnitIdIn(List<Long> unitIds);
}
