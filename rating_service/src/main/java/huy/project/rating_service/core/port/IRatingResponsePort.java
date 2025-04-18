package huy.project.rating_service.core.port;

import huy.project.rating_service.core.domain.entity.RatingResponseEntity;

import java.util.List;

public interface IRatingResponsePort {
    RatingResponseEntity save(RatingResponseEntity ratingResponse);
    List<RatingResponseEntity> getRatingResponsesByRatingIds(List<Long> ratingIds);
}
