package huy.project.accommodation_service.core.domain.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceCalculationRequest {
    Long unitId;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    Integer guestCount;
}
