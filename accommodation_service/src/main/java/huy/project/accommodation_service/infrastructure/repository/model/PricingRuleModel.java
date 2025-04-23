package huy.project.accommodation_service.infrastructure.repository.model;

import huy.project.accommodation_service.core.domain.enums.PricingRuleType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @Column(name = "unit_id")
    Long unitId;

    @Column(name = "rule_type")
    @Enumerated(EnumType.STRING)
    PricingRuleType ruleType;

    @Column(name = "start_date")
    LocalDate startDate;

    @Column(name = "end_date")
    LocalDate endDate;

    @Column(name = "adjustment_type")
    String adjustmentType; // PERCENTAGE, FIXED_AMOUNT

    @Column(name = "adjustment_value")
    Long adjustmentValue;

    @Column(name = "min_nights")
    Integer minNights;

    @Column(name = "max_nights")
    Integer maxNights;

    @Column(name = "min_occupancy")
    Integer minOccupancy;

    @Column(name = "max_occupancy")
    Integer maxOccupancy;

    @Column(name = "promotion_code")
    String promotionCode;

    @Column(name = "promotion_description")
    String promotionDescription;

    @Column(name = "is_active")
    Boolean isActive;

    @Column(name = "priority")
    Integer priority; // Higher priority rules are applied first
}
