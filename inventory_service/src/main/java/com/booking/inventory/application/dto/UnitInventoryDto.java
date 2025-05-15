package com.booking.inventory.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitInventoryDto {
    private Long id;
    private Long accommodationId;
    private Long unitId;
    private String unitName;
    private Integer quantity;
    private Integer availableRooms;
    private List<String> roomNumbers;
}
