package huy.project.payment_service.core.usecase;

import huy.project.payment_service.core.domain.dto.request.CreatePaymentDto;
import huy.project.payment_service.core.domain.entity.PaymentEntity;
import huy.project.payment_service.core.domain.enums.PaymentStatus;
import huy.project.payment_service.core.port.IPaymentPort;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CreatePaymentUseCase {
    IPaymentPort paymentPort;

    @Transactional
    public PaymentEntity createPayment(Long userId, CreatePaymentDto request) {
        PaymentEntity payment = PaymentEntity.builder()
                .bookingId(1L)
                .amount(request.getAmount())
                .userId(userId)
                .status(PaymentStatus.PENDING)
                .build();
        return paymentPort.save(payment);
    }
}
