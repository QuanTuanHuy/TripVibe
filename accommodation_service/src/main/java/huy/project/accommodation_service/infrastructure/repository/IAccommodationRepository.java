package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.AccommodationModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAccommodationRepository extends IBaseRepository<AccommodationModel> {
    Optional<AccommodationModel> findByName(String name);
}
