package huy.project.accommodation_service.core.domain.entity;

import huy.project.accommodation_service.core.domain.enums.PricingRuleType;
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
    Long unitId;
    PricingRuleType ruleType;
    LocalDate startDate;
    LocalDate endDate;
    String adjustmentType; // PERCENTAGE, FIXED_AMOUNT
    BigDecimal adjustmentValue;
    Integer minNights;
    Integer maxNights;
    Integer minOccupancy;
    Integer maxOccupancy;
    String promotionCode;
    String promotionDescription;
    Boolean isActive;
    Integer priority; // Higher priority rules are applied first
}
