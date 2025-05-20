package huy.project.inventory_service.ui.resource.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConfirmBookingRequest {
    private String lockId;
    private Long bookingId;
}
