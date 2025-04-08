package huy.project.payment_service.core.service;

import huy.project.payment_service.core.domain.dto.request.CreatePaymentDto;
import huy.project.payment_service.core.domain.entity.PaymentEntity;

public interface IPaymentService {
    PaymentEntity createPayment(Long userId, CreatePaymentDto request);
}
