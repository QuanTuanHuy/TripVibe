package huy.project.payment_service.infrastructure.repository;

import huy.project.payment_service.core.domain.enums.PaymentStatus;
import huy.project.payment_service.infrastructure.repository.model.PaymentModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPaymentRepository extends IBaseRepository<PaymentModel> {
    List<PaymentModel> findByBookingId(Long bookingId);
    List<PaymentModel> findByBookingIdAndStatus(Long bookingId, PaymentStatus status);
}
