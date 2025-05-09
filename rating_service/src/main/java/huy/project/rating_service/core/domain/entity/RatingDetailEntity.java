package huy.project.rating_service.core.domain.entity;

import huy.project.rating_service.core.domain.constant.RatingCriteriaType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatingDetailEntity {
    Long id;
    Long ratingId;
    RatingCriteriaType criteriaType;
    Double value;
}
