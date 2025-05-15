package com.booking.inventory.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingConfirmationRequest {
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String sessionId;
    private String bookingId;
}
