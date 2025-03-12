package huy.project.profile_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLocationDto {
    private Long countryId;
    private Long provinceId;
    private Long longitude;
    private Long latitude;
    private String address;
}
