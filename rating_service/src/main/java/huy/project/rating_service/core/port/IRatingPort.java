package huy.project.rating_service.core.port;

import huy.project.rating_service.core.domain.entity.RatingEntity;

public interface IRatingPort {
    RatingEntity save(RatingEntity rating);
    RatingEntity getRatingByUnitIdAndUserId(Long unitId, Long userId);
}
