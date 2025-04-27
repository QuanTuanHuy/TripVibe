package huy.project.accommodation_service.core.domain.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PricingRuleParams {
    Long accommodationId;
    Long unitId;
    LocalDate startDate;
    LocalDate endDate;
}
