package huy.project.rating_service.infrastructure.repository;

import huy.project.rating_service.infrastructure.repository.model.MonthlyRatingModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMonthlyRatingRepository extends IBaseRepository<MonthlyRatingModel> {
    List<MonthlyRatingModel> findByRatingTrendId(Long ratingTrendId);
    Optional<MonthlyRatingModel> findByRatingTrendIdAndYearMonth(Long ratingTrendId, String yearMonth);

}
