package huy.project.rating_service.core.domain.dto.request;

import huy.project.rating_service.core.domain.constant.RatingCriteriaType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CreateRatingDto {
    private Long userId;

    @NotNull(message = "Accommodation ID không được để trống")
    @Positive(message = "Accommodation ID phải là số dương")
    private Long accommodationId;

    @NotNull(message = "Unit ID không được để trống")
    @Positive(message = "Unit ID phải là số dương")
    private Long unitId;

    @NotNull(message = "Booking ID không được để trống")
    @Positive(message = "Booking ID phải là số dương")
    private Long bookingId;

    @NotNull(message = "Rating value không được để trống")
    @DecimalMin(value = "1.0", message = "Rating value phải lớn hơn hoặc bằng 1")
    @DecimalMax(value = "10.0", message = "Rating value phải nhỏ hơn hoặc bằng 10")
    private Double value;

    @Size(max = 1000, message = "Comment không được vượt quá 1000 ký tự")
    private String comment;

    private Long languageId;

    @NotNull(message = "Rating details không được để trống")
    @NotEmpty(message = "Rating details không được để trống")
    private Map<RatingCriteriaType, Double> ratingDetails;

    @AssertTrue(message = "Giá trị trong rating details phải nằm trong khoảng từ 1 đến 10")
    private boolean isRatingDetailsValid() {
        if (ratingDetails == null) {
            return true; // Để @NotNull xử lý
        }

        for (Map.Entry<RatingCriteriaType, Double> entry : ratingDetails.entrySet()) {
            Double value = entry.getValue();
            if (value == null || value < 1 || value > 10) {
                return false;
            }
        }
        return true;
    }
}
