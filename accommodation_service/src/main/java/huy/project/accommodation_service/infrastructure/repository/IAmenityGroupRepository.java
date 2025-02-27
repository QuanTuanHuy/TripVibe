package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.AmenityGroupModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAmenityGroupRepository extends IBaseRepository<AmenityGroupModel> {
    Optional<AmenityGroupModel> findByName(String name);
}
