package huy.project.rating_service.core.port;

import huy.project.rating_service.core.domain.entity.RatingHelpfulnessEntity;

public interface IRatingHelpfulnessPort {
    RatingHelpfulnessEntity save(RatingHelpfulnessEntity ratingHelpfulness);
    RatingHelpfulnessEntity getByUserIdAndRatingId(Long userId, Long ratingId);
}
