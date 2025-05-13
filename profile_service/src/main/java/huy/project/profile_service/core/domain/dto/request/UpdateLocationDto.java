package huy.project.profile_service.core.domain.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLocationDto {
    @Positive(message = "countryId phải lớn hơn 0")
    private Long countryId;
    @Positive(message = "provinceId phải lớn hơn 0")
    private Long provinceId;

    @Positive(message = "longitude phải lớn hơn 0")
    private Double longitude;
    @Positive(message = "latitude phải lớn hơn 0")
    private Double latitude;
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;
}
