package huy.project.payment_service.core.service.impl;

import huy.project.payment_service.core.domain.dto.PaymentWebhookDto;
import huy.project.payment_service.core.domain.dto.request.CreatePaymentDto;
import huy.project.payment_service.core.domain.entity.PaymentEntity;
import huy.project.payment_service.core.service.IPaymentService;
import huy.project.payment_service.core.usecase.CancelPaymentUseCase;
import huy.project.payment_service.core.usecase.CreatePaymentUseCase;
import huy.project.payment_service.core.usecase.GetPaymentUseCase;
import huy.project.payment_service.core.usecase.ProcessPaymentWebhookUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService implements IPaymentService {
    CreatePaymentUseCase createPaymentUseCase;
    GetPaymentUseCase getPaymentUseCase;
    CancelPaymentUseCase cancelPaymentUseCase;
    ProcessPaymentWebhookUseCase processPaymentWebhookUseCase;

    @Override
    public PaymentEntity createPayment(Long userId, CreatePaymentDto request) {
        return createPaymentUseCase.createPayment(userId, request);
    }

    @Override
    public PaymentEntity getPaymentById(Long id) {
        return getPaymentUseCase.getPaymentById(id);
    }

    @Override
    public void cancelPayment(Long id) {
        cancelPaymentUseCase.cancelPayment(id);
    }

    @Override
    public PaymentEntity processPaymentWebhook(PaymentWebhookDto webhookDto, String rawPayload) {
        return processPaymentWebhookUseCase.processPaymentWebhook(webhookDto, rawPayload);
    }
}
