package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateUnitPriceGroup {
    private Long numberOfGuests;
    private Long percentage;
}
