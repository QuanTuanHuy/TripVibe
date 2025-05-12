package huy.project.rating_service.core.port;

import huy.project.rating_service.core.domain.constant.RatingCriteriaType;
import huy.project.rating_service.core.domain.entity.RatingDetailEntity;

import java.util.List;
import java.util.Map;

public interface IRatingDetailPort {
    List<RatingDetailEntity> saveAll(List<RatingDetailEntity> ratingDetails);

    List<RatingDetailEntity> getByRatingId(Long ratingId);

    void deleteByRatingId(Long ratingId);

    Map<RatingCriteriaType, Double> getAverageRatingsByCriteriaForAccommodation(Long accommodationId);
}
