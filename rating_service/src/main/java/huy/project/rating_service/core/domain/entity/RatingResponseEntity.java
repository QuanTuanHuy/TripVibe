package huy.project.rating_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RatingResponseEntity {
    private Long id;
    private Long ratingId;
    private Long ownerId;
    private String content;
    private Long createdAt;
}
