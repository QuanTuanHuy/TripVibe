package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.AccommodationModel;
import org.springframework.stereotype.Repository;

@Repository
public interface IAccommodationRepository extends IBaseRepository<AccommodationModel> {
}
