package huy.project.accommodation_service.core.domain.dto.request;

import huy.project.accommodation_service.core.domain.enums.PricingRuleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class CreatePricingRuleDto {
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
    Integer priority; // Higher priority rules are applied first
}
