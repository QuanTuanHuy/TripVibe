package huy.project.rating_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RatingHelpfulnessEntity {
    private Long id;
    private Long ratingId;
    private Long userId;
    private Boolean isHelpful;
}