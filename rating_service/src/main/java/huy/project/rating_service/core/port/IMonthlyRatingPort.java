package huy.project.rating_service.core.port;

import huy.project.rating_service.core.domain.entity.MonthlyRatingEntity;

import java.util.List;

public interface IMonthlyRatingPort {
    void save(MonthlyRatingEntity monthlyRating);
    List<MonthlyRatingEntity> getByRatingTrendId(Long ratingTrendId);
    MonthlyRatingEntity getByRatingTrendIdAndMonth(Long ratingTrendId, String month);
}
