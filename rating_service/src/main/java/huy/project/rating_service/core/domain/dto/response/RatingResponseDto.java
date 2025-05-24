package huy.project.rating_service.core.domain.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RatingResponseDto {
    private Long id;
    private Long ratingId;
    private String content;
    private Long createdAt;
}
