package huy.project.accommodation_service.infrastructure.repository.model;

import huy.project.accommodation_service.core.domain.enums.PricingRuleType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "pricing_rules")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PricingRuleModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "accommodation_id")
    Long accommodationId;

    @Column(name = "unit_id")
    Long unitId;

    @Column(name = "name")
    String name;

    @Column(name = "rule_type")
    @Enumerated(EnumType.STRING)
    PricingRuleType ruleType;

    @Column(name = "start_date")
    LocalDate startDate;

    @Column(name = "end_date")
    LocalDate endDate;

    @Column(name = "apply_monday")
    Boolean applyMonday;
    @Column(name = "apply_tuesday")
    Boolean applyTuesday;
    @Column(name = "apply_wednesday")
    Boolean applyWednesday;
    @Column(name = "apply_thursday")
    Boolean applyThursday;
    @Column(name = "apply_friday")
    Boolean applyFriday;
    @Column(name = "apply_saturday")
    Boolean applySaturday;
    @Column(name = "apply_sunday")
    Boolean applySunday;

    @Column(name = "price_multiplier")
    BigDecimal priceMultiplier;
    @Column(name = "fixed_adjustment")
    BigDecimal fixedAdjustment;
    @Column(name = "day_threshold")
    Integer dayThreshold; // For early bird, last minute, or LOS
    @Column(name = "additional_params", columnDefinition = "TEXT")
    String additionalParams; // JSON for complex rule parameters

    @Column(name = "is_active")
    Boolean isActive;

    @Column(name = "priority")
    Integer priority; // Higher priority rules are applied first
}
