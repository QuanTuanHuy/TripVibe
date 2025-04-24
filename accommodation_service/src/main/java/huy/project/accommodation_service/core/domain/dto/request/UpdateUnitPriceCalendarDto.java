package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateUnitPriceCalendarDto {
    private Long startDate;
    private Long endDate;
    private BigDecimal price;
}
