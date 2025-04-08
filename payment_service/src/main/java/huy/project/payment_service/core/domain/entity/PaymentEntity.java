package huy.project.payment_service.core.domain.entity;

import huy.project.payment_service.core.domain.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PaymentEntity {
    private Long id;
    private Long bookingId;
    private Long userId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionId;
    private String gatewayReferenceId;
    private String paymentMethod;
    private String paymentGateway;
    private String currency;
    private String paymentUrl;
    private String gatewayResponse;
    private Long createdAt;
    private Long updatedAt;
    private Long paidAt;
}
