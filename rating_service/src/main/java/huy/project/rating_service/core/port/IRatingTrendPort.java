package huy.project.rating_service.core.port;

import huy.project.rating_service.core.domain.entity.RatingTrendEntity;

public interface IRatingTrendPort {
    void save(RatingTrendEntity ratingTrend);
    RatingTrendEntity getRatingTrendByAccId(Long accId);
}
