package huy.project.inventory_service.core.domain.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequest {
    private Long bookingId;
    private LocalDate checkInDate;
    private Long guestId;
    private Integer guestCount;
    private String specialRequests;
}