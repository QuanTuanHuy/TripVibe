package huy.project.rating_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MonthlyRatingEntity {
    private String yearMonth; // "2023-10"
    private Integer ratingCount;
    private Long totalRating;
    private Long ratingTrendId;
}
