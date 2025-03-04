package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.AccommodationAmenityModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAccommodationAmenityRepository extends IBaseRepository<AccommodationAmenityModel> {
    List<AccommodationAmenityModel> findByAccommodationId(Long accommodationId);
}
