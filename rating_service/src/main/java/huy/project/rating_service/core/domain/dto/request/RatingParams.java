package huy.project.rating_service.core.domain.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class RatingParams extends BaseParams {
    @DecimalMin(value = "1.0", message = "Giá trị tối thiểu phải lớn hơn hoặc bằng 1")
    @DecimalMax(value = "10.0", message = "Giá trị tối đa phải nhỏ hơn hoặc bằng 10")
    private Double minValue;

    @DecimalMin(value = "1.0", message = "Giá trị tối thiểu phải lớn hơn hoặc bằng 1")
    @DecimalMax(value = "10.0", message = "Giá trị tối đa phải nhỏ hơn hoặc bằng 10")
    private Double maxValue;

    @Positive(message = "Language ID phải là số dương")
    private Long languageId;

    @Positive(message = "createdFrom phải là số dương")
    private Long createdFrom;

    @Positive(message = "createdTo phải là số dương")
    private Long createdTo;

    @Positive(message = "Accommodation ID phải là số dương")
    private Long accommodationId;

    @Positive(message = "Unit ID phải là số dương")
    private Long unitId;

    // Custom validation cho khoảng giá trị min/max
    @AssertTrue(message = "minValue không được lớn hơn maxValue")
    private boolean isValidValueRange() {
        if (minValue == null || maxValue == null) {
            return true;
        }
        return minValue <= maxValue;
    }

    // Custom validation cho khoảng thời gian
    @AssertTrue(message = "createdFrom không được lớn hơn createdTo")
    private boolean isValidTimeRange() {
        if (createdFrom == null || createdTo == null) {
            return true;
        }
        return createdFrom <= createdTo;
    }
}