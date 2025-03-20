package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LocationEntity {
    private Long id;
    private Long countryId;
    private Long provinceId;
    private Double longitude;
    private Double latitude;
    private String detailAddress;
}
