package huy.project.inventory_service.core.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmBookingResponse {
    private boolean success;
    private Long bookingId;
    private LocalDateTime confirmedAt;
    private List<String> errors;
}
