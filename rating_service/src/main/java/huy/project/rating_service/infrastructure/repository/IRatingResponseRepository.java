package huy.project.rating_service.infrastructure.repository;

import huy.project.rating_service.infrastructure.repository.model.RatingResponseModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRatingResponseRepository extends IBaseRepository<RatingResponseModel> {
    List<RatingResponseModel> findByRatingIdIn(List<Long> ratingIds);
}
