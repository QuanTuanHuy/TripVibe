package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.AccommodationTypeModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAccommodationTypeRepository extends IBaseRepository<AccommodationTypeModel> {
    Optional<AccommodationTypeModel> findByName(String name);
}
