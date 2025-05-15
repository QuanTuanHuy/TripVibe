package com.booking.inventory.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitInventorySyncRequest {
    private Long accommodationId;
    private Long unitId;
    private String unitName;
    private Integer quantity;
    private Long roomTypeId;
}
