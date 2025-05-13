package huy.project.rating_service.core.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRatingResponseDto {
    @NotNull(message = "Rating ID không được để trống")
    private Long ratingId;

    @Size(max = 1000, message = "Nội dung phản hồi không được vượt quá 1000 ký tự")
    @NotNull(message = "Nội dung phản hồi không được để trống")
    private String content;
}
