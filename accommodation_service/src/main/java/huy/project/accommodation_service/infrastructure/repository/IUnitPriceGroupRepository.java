package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.UnitPriceGroupModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUnitPriceGroupRepository extends IBaseRepository<UnitPriceGroupModel> {
    List<UnitPriceGroupModel> findByUnitIdIn(List<Long> unitIds);
}
