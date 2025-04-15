package huy.project.rating_service.infrastructure.repository;

import huy.project.rating_service.infrastructure.repository.model.RatingHelpfulnessModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRatingHelpfulnessRepository extends IBaseRepository<RatingHelpfulnessModel> {
    Optional<RatingHelpfulnessModel> findByUserIdAndRatingId(Long userId, Long ratingId);
}
