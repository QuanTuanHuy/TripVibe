package huy.project.payment_service.core.usecase;

import huy.project.payment_service.core.domain.constant.ErrorCode;
import huy.project.payment_service.core.domain.dto.response.PaymentGatewayResult;
import huy.project.payment_service.core.domain.enums.PaymentStatus;
import huy.project.payment_service.core.exception.AppException;
import huy.project.payment_service.core.port.IPaymentPort;
import huy.project.payment_service.core.usecase.strategy.PaymentGatewayFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CancelPaymentUseCase {
    IPaymentPort paymentPort;
    GetPaymentUseCase getPaymentUseCase;
    PaymentGatewayFactory paymentGatewayFactory;

    @Transactional(rollbackFor = Exception.class)
    public void cancelPayment(Long id) {
        var payment = getPaymentUseCase.getPaymentById(id);

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            log.error("Cannot cancel payment with id {} because it is already completed", id);
            throw new AppException(ErrorCode.PAYMENT_ALREADY_COMPLETED);
        }

        var gateway = paymentGatewayFactory.getGateway(payment.getPaymentGateway());
        PaymentGatewayResult gatewayResult = gateway.cancelPayment(payment);

        payment.setStatus(PaymentStatus.CANCELLED);

        if (gatewayResult.getRawResponse() != null) {
            payment.setGatewayResponse(gatewayResult.getRawResponse());
        }

        paymentPort.save(payment);
        log.info("Cancel payment {} success", id);
    }
}
