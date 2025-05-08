package huy.project.rating_service.core.domain.dto.request;

import huy.project.rating_service.core.domain.constant.RatingCriteriaType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CreateRatingDto {
    private Long userId;
    private Long accommodationId;
    private Long unitId;
    private Long bookingId;
    private Double value;
    private String comment;
    private Long languageId;

    private Map<RatingCriteriaType, Double> ratingDetails;
}
