package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UnitAmenityEntity {
    private Long id;
    private Long unitId;
    private Long amenityId;
    private Long fee;
    private Boolean needToReserve;

    private AmenityEntity amenity;
}
