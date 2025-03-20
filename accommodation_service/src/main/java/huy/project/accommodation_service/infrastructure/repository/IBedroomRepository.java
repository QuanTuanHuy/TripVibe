package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.BedroomModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBedroomRepository extends IBaseRepository<BedroomModel> {
    List<BedroomModel> findByUnitIdIn(List<Long> unitIds);
    List<BedroomModel> findByUnitId(Long unitId);
}
