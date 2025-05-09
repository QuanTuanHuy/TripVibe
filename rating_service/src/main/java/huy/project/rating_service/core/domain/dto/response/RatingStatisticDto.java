package huy.project.rating_service.core.domain.dto.response;

import huy.project.rating_service.core.domain.constant.RatingCriteriaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RatingStatisticDto {
    private Long accommodationId;
    private Double overallAverage;
    private Integer totalRatings;
    private Map<Integer, Integer> ratingDistribution;
    private Map<RatingCriteriaType, Double> criteriaAverages;
}
