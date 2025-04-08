package huy.project.payment_service.core.port;

import huy.project.payment_service.core.domain.entity.PaymentEntity;
import huy.project.payment_service.core.domain.enums.PaymentStatus;

import java.util.List;

public interface IPaymentPort {
    PaymentEntity save(PaymentEntity payment);
    List<PaymentEntity> saveAll(List<PaymentEntity> payments);
    List<PaymentEntity> getPaymentsByBookingIdAndStatus(Long bookingId, PaymentStatus status);
    PaymentEntity getPaymentById(Long id);
    PaymentEntity getPaymentByTransactionId(String transactionId);
    List<PaymentEntity> getPaymentsByBookingId(Long bookingId);
}
