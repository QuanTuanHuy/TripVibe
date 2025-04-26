package huy.project.accommodation_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "unit_price_calendars")
public class UnitPriceCalendarModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "price")
    private BigDecimal price;

    // Dynamic pricing factors
    @Column(name = "weekday_multiplier")
    BigDecimal weekendMultiplier;
    @Column(name = "holiday_multiplier")
    BigDecimal holidayMultiplier;
    @Column(name = "seasonType")
    String seasonType; // "HIGH", "LOW", "SHOULDER", etc.
    @Column(name = "seasonal_multiplier")
    BigDecimal seasonalMultiplier;

    // LOS (Length of Stay) pricing
    @Column(name = "los_discounts")
    String losDiscounts; // JSON: {"3": 0.05, "7": 0.10, "30": 0.15}

    // Early bird and last minute
    @Column(name = "early_bird_days")
    Integer earlyBirdDays; // Days in advance for early bird pricing
    @Column(name = "early_bird_discount")
    BigDecimal earlyBirdDiscount;
    @Column(name = "last_minute_days")
    Integer lastMinuteDays; // Days before for last-minute deals
    @Column(name = "last_minute_discount")
    BigDecimal lastMinuteDiscount;

    // Occupancy-based pricing
    @Column(name = "base_occupancy")
    Integer baseOccupancy; // Standard occupancy this price is for
    @Column(name = "extra_person_fee")
    BigDecimal extraPersonFee; // Fee per additional guest

    @Column(name = "last_updated")
    Long lastUpdated;
    @Column(name = "update_source")
    String updateSource; // "MANUAL", "RULE", "CHANNEL", "PMS"
}
