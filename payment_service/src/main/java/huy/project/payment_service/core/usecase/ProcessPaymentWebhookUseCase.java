package huy.project.payment_service.core.usecase;

import huy.project.payment_service.core.domain.constant.ErrorCode;
import huy.project.payment_service.core.domain.dto.PaymentWebhookDto;
import huy.project.payment_service.core.domain.dto.response.PaymentGatewayResult;
import huy.project.payment_service.core.domain.entity.PaymentEntity;
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

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProcessPaymentWebhookUseCase {
    IPaymentPort paymentPort;
    GetPaymentUseCase getPaymentUseCase;
    PaymentGatewayFactory paymentGatewayFactory;

    @Transactional(rollbackFor = Exception.class)
    public PaymentEntity processPaymentWebhook(PaymentWebhookDto webhookDto, String rawPayload) {
        log.info("Processing payment webhook: {}", webhookDto);

        PaymentEntity payment = getPaymentUseCase.getPaymentByTransactionId(webhookDto.getTransactionId());

        var gateway = paymentGatewayFactory.getGateway(payment.getPaymentGateway());

        PaymentGatewayResult gatewayResult = gateway.processWebhook(webhookDto, rawPayload);

        if (!gatewayResult.isSuccess()) {
            log.error("Failed to process payment webhook: {}", gatewayResult.getMessage());
            throw new AppException(ErrorCode.PROCESS_PAYMENT_WEBHOOK_FAILED);
        }

        payment.setStatus(gatewayResult.getStatus());

        if (gatewayResult.getStatus() == PaymentStatus.COMPLETED) {
            payment.setPaidAt(Instant.now().toEpochMilli());
        }
        if (gatewayResult.getGatewayReferenceId() != null) {
            payment.setGatewayReferenceId(gatewayResult.getGatewayReferenceId());
        }

        return paymentPort.save(payment);
    }
}
