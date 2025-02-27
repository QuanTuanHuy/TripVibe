package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.AmenityModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAmenityRepository extends IBaseRepository<AmenityModel> {
    Optional<AmenityModel> findByName(String name);
}
