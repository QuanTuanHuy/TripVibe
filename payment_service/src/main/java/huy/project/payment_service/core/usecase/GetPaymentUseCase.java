package huy.project.payment_service.core.usecase;

import huy.project.payment_service.core.domain.constant.ErrorCode;
import huy.project.payment_service.core.domain.entity.PaymentEntity;
import huy.project.payment_service.core.exception.AppException;
import huy.project.payment_service.core.port.IPaymentPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GetPaymentUseCase {
    IPaymentPort paymentPort;

    public PaymentEntity getPaymentById(Long id) {
        var payment = paymentPort.getPaymentById(id);
        if (payment == null) {
            log.error("payment not found with id: {}", id);
            throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return payment;
    }

    public PaymentEntity getPaymentByTransactionId(String transactionId) {
        var payment = paymentPort.getPaymentByTransactionId(transactionId);
        if (payment == null) {
            log.error("payment not found with transactionId: {}", transactionId);
            throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return payment;
    }
}
