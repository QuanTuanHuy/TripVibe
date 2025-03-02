package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UnitPriceGroupEntity {
    private Long id;
    private Long unitId;
    private Long numberOfGuests;
    private Long percentage;
}
