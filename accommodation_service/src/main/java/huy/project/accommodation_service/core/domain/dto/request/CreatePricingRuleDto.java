package huy.project.accommodation_service.core.domain.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePricingRuleDto {
    Long accommodationId; // Can be null if applies to all properties
    Long unitId;  // Can be null if applies to all units

    String name;
    String ruleType;

    LocalDate startDate;
    LocalDate endDate;

    // Day of week applicable (for weekend rules)
    Boolean applyMonday;
    Boolean applyTuesday;
    Boolean applyWednesday;
    Boolean applyThursday;
    Boolean applyFriday;
    Boolean applySaturday;
    Boolean applySunday;

    BigDecimal priceMultiplier;
    BigDecimal fixedAdjustment;
    Integer dayThreshold; // For early bird, last minute, or LOS
    String additionalParams; // JSON for complex rule parameters

    Integer priority; // Higher priority rules are applied first
}
