package huy.project.search_service.infrastructure.repository.document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDocument {
    private Double longitude;
    private Double latitude;
    private Long provinceId;

    public String asGeoPoint() {
        return latitude + "," + longitude;
    }
}