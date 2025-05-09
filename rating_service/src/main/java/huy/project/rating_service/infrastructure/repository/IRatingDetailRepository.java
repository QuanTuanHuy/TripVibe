package huy.project.rating_service.infrastructure.repository;

import huy.project.rating_service.infrastructure.repository.model.RatingDetailModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRatingDetailRepository extends IBaseRepository<RatingDetailModel> {
    List<RatingDetailModel> findByRatingId(Long ratingId);

    void deleteByRatingId(Long ratingId);

    @Query("SELECT rd.criteriaType as criteriaType, AVG(rd.value) as averageValue " +
            "FROM RatingDetailModel rd " +
            "JOIN RatingModel r ON rd.ratingId = r.id " +
            "WHERE r.accommodationId = :accommodationId " +
            "GROUP BY rd.criteriaType")
    List<Object[]> getAverageRatingsByCriteriaForAccommodation(Long accommodationId);
}
