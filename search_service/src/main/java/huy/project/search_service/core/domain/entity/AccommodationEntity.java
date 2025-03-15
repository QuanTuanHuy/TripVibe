package huy.project.search_service.core.domain.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccommodationEntity {
    private Long id;
    private Long typeId;
    private String name;
    private Double ratingStar;
    private Boolean isVerified;

    private List<Long> amenityIds;
    private List<Long> bookingPolicyIds;

    private LocationEntity location;
    private List<UnitEntity> units;
}
