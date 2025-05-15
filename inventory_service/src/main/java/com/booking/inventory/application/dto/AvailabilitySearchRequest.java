package com.booking.inventory.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilitySearchRequest {
    private Long propertyId;
    private Long roomTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer adults;
    private Integer children;
}
