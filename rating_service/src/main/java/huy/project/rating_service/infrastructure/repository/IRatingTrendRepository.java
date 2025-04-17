package huy.project.rating_service.infrastructure.repository;

import huy.project.rating_service.infrastructure.repository.model.RatingTrendModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRatingTrendRepository extends IBaseRepository<RatingTrendModel> {
    Optional<RatingTrendModel> findByAccommodationId(Long accId);
}
