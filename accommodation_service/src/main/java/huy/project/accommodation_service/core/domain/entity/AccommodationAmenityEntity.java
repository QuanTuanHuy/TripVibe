package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccommodationAmenityEntity {
    private Long id;
    private Long accommodationId;
    private Long amenityId;
    private Long fee;
    private Boolean needToReserve;

    private AmenityEntity amenity;
}
