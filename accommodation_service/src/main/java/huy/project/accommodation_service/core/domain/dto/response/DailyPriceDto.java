package huy.project.accommodation_service.core.domain.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DailyPriceDto {
    LocalDate date;
    BigDecimal price;
}
