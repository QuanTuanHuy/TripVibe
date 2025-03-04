package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.BedTypeModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IBedTypeRepository extends IBaseRepository<BedTypeModel> {
    Optional<BedTypeModel> findByName(String name);
    List<BedTypeModel> findByIdIn(List<Long> ids);
}
