package huy.project.rating_service.core.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateRatingHelpfulnessDto {
    private Long ratingId;
    private Boolean isHelpful;
}
