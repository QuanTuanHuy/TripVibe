package huy.project.profile_service.core.domain.entity;

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
    private Long longitude;
    private Long latitude;
    private String address;
}
