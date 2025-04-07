package huy.project.rating_service.core.domain.entity;

import lombok.*;

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
}
