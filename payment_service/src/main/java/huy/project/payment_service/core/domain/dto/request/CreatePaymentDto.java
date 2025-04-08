package huy.project.payment_service.core.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentDto {
    private Long bookingId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String paymentGateway; // Add this field for gateway selection
    private String returnUrl;
    private String cancelUrl;
    private String customerEmail; // Useful for some payment gateways
    private String description;   // Payment description
}
