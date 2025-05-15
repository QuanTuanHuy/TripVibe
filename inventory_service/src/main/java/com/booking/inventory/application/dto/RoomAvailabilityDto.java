package com.booking.inventory.application.dto;

import com.booking.inventory.domain.model.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAvailabilityDto {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private String roomName;
    private Long propertyId;
    private String propertyName;
    private Long roomTypeId;
    private String roomTypeName;
    private LocalDate date;
    private RoomStatus status;
    private BigDecimal price;
    private String bookingId;
}
