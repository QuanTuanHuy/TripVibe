package huy.project.payment_service.core.service.impl;

import huy.project.payment_service.core.domain.dto.request.CreatePaymentDto;
import huy.project.payment_service.core.domain.entity.PaymentEntity;
import huy.project.payment_service.core.service.IPaymentService;
import huy.project.payment_service.core.usecase.CreatePaymentUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService implements IPaymentService {
    CreatePaymentUseCase createPaymentUseCase;

    @Override
    public PaymentEntity createPayment(Long userId, CreatePaymentDto request) {
        return createPaymentUseCase.createPayment(userId, request);
    }
}
