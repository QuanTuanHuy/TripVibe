package huy.project.rating_service.core.domain.entity;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RatingTrendEntity {
    private Long id;
    private Long accommodationId;
    private List<MonthlyRatingEntity> monthlyRatings;
}
