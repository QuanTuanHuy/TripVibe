package huy.project.accommodation_service.core.domain.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceCalculationResponse {
    Long unitId;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    Integer guestCount;
    BigDecimal basePrice;
    BigDecimal totalPrice;
    List<DailyPriceDto> dailyPrices;
}
