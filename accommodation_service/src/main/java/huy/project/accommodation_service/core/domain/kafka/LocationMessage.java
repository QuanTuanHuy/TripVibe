package huy.project.accommodation_service.core.domain.kafka;

import huy.project.accommodation_service.core.domain.entity.LocationEntity;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LocationMessage {
    private Double longitude;
    private Double latitude;
    private Long provinceId;

    public static LocationMessage from(LocationEntity location) {
        return LocationMessage.builder()
                .longitude(location.getLongitude())
                .latitude(location.getLatitude())
                .provinceId(location.getProvinceId())
                .build();
    }
}
