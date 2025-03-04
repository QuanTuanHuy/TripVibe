package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateLocationDto {
    private Long countryId;
    private Long provinceId;
    private Long longitude;
    private Long latitude;
    private String detailAddress;
}
