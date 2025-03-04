package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UnitPriceTypeEntity {
    private Long id;
    private Long unitId;
    private Long priceTypeId;
    private Long percentage;

    private PriceTypeEntity priceType;
}
