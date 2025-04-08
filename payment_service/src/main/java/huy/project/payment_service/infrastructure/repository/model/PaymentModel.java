package huy.project.payment_service.infrastructure.repository.model;

import huy.project.payment_service.core.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "gateway_reference_id")
    private String gatewayReferenceId;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_gateway")
    private String paymentGateway;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "payment_url")
    private String paymentUrl;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "paid_at")
    private Instant paidAt;
}