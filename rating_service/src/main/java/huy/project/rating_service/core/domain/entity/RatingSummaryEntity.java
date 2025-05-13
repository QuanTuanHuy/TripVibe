package huy.project.rating_service.core.domain.entity;

import huy.project.rating_service.core.domain.constant.RatingCriteriaType;
import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RatingSummaryEntity {
    private Long id;
    private Long accommodationId;
    private Integer numberOfRatings;
    private Long totalRating;
    private Boolean isSyncedWithSearchService;

    private Map<Integer, Integer> distribution;
    private Map<RatingCriteriaType, Double> criteriaAverages;
}
