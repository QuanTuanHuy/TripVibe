package huy.project.search_service.core.domain.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitEntity {
    private Long id;
    private Integer maxAdults;
    private Integer maxChildren;
    private Long pricePerNight;

    private List<Long> amenityIds;
    private List<BedroomEntity> bedrooms;
    private List<UnitAvailabilityEntity> availability;
}
