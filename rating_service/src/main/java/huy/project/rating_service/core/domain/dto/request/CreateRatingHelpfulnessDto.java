package huy.project.rating_service.core.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateRatingHelpfulnessDto {
    @NotNull(message = "Rating ID không được để trống")
    private Long ratingId;

    @NotNull(message = "isHelpful không được để trống")
    private Boolean isHelpful;
}