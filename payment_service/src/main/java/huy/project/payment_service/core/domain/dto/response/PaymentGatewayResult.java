package huy.project.payment_service.core.domain.dto.response;

import huy.project.payment_service.core.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGatewayResult {
    private boolean success;
    private String gatewayReferenceId;
    private String paymentUrl;
    private PaymentStatus status;
    private String message;
    private String rawResponse;

    @Builder.Default
    private Map<String, Object> additionalData = new HashMap<>();
}