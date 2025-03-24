package huy.project.rating_service.infrastructure.repository;

import huy.project.rating_service.infrastructure.repository.model.RatingModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRatingRepository extends IBaseRepository<RatingModel> {
    Optional<RatingModel> findByUnitIdAndUserId(Long unitId, Long userId);
}
