package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.UnitNameModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUnitNameRepository extends IBaseRepository<UnitNameModel> {
    Optional<UnitNameModel> findByName(String name);
}
