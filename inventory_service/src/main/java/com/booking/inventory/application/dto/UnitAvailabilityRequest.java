package com.booking.inventory.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitAvailabilityRequest {
    private Long unitId;
    private LocalDate startDate;
    private LocalDate endDate;
}
