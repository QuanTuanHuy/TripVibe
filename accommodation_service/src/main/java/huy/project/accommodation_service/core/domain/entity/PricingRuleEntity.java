package huy.project.accommodation_service.core.domain.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PricingRuleEntity {
    Long id;

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

    Boolean isActive;
    Integer priority; // Higher priority rules are applied first
}
