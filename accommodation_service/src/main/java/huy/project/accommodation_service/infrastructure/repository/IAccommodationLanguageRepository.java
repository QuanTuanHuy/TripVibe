package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.AccommodationLanguageModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAccommodationLanguageRepository extends IBaseRepository<AccommodationLanguageModel> {
    List<AccommodationLanguageModel> findByAccommodationId(Long accommodationId);
}