package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class CreateUnitPriceTypeDto {
    private Long priceTypeId;
    private Long percentage;
}
