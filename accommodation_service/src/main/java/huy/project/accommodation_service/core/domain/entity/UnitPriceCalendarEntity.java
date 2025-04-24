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
public class UnitPriceCalendarEntity {
    Long id;
    Long unitId;
    LocalDate date;
    BigDecimal basePrice;

    // Dynamic pricing factors
    BigDecimal weekendMultiplier;
    BigDecimal holidayMultiplier;
    String seasonType; // "HIGH", "LOW", "SHOULDER", etc.
    BigDecimal seasonalMultiplier;

    // LOS (Length of Stay) pricing
    String losDiscounts; // JSON: {"3": 0.05, "7": 0.10, "30": 0.15}

    // Early bird and last minute
    Integer earlyBirdDays; // Days in advance for early bird pricing
    BigDecimal earlyBirdDiscount;
    Integer lastMinuteDays; // Days before for last-minute deals
    BigDecimal lastMinuteDiscount;

    // Occupancy-based pricing
    BigDecimal baseOccupancy; // Standard occupancy this price is for
    BigDecimal extraPersonFee; // Fee per additional guest

    Long lastUpdated;
    String updateSource; // "MANUAL", "RULE", "CHANNEL", "PMS"
}
