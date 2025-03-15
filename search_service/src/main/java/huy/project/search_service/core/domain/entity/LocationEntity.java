package huy.project.search_service.core.domain.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationEntity {
    private Double longitude;
    private Double latitude;
    private Long provinceId;
}
