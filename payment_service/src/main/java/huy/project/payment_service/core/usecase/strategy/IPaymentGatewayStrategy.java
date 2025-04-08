package huy.project.payment_service.core.usecase.strategy;

import huy.project.payment_service.core.domain.dto.PaymentWebhookDto;
import huy.project.payment_service.core.domain.dto.request.CreatePaymentDto;
import huy.project.payment_service.core.domain.dto.response.PaymentGatewayResult;
import huy.project.payment_service.core.domain.entity.PaymentEntity;

public interface IPaymentGatewayStrategy {
    /**
     * Creates a payment with the gateway and returns the payment URL and reference ID
     */
    PaymentGatewayResult createPayment(CreatePaymentDto requestDto, PaymentEntity payment);

    /**
     * Processes a webhook notification from the payment gateway
     */
    PaymentGatewayResult processWebhook(PaymentWebhookDto webhookDto, String rawPayload);

    /**
     * Verifies the payment status with the gateway
     */
    PaymentGatewayResult verifyPayment(String gatewayReferenceId);

    /**
     * Cancels a payment with the gateway
     */
    PaymentGatewayResult cancelPayment(PaymentEntity payment);

    /**
     * Returns the name of this gateway implementation
     */
    String getGatewayName();
}
