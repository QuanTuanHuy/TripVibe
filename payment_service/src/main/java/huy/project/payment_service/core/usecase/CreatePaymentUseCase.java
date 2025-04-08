package huy.project.payment_service.core.usecase;

import huy.project.payment_service.core.domain.constant.ErrorCode;
import huy.project.payment_service.core.domain.dto.request.CreatePaymentDto;
import huy.project.payment_service.core.domain.dto.response.PaymentGatewayResult;
import huy.project.payment_service.core.domain.entity.PaymentEntity;
import huy.project.payment_service.core.domain.enums.PaymentStatus;
import huy.project.payment_service.core.exception.AppException;
import huy.project.payment_service.core.port.IPaymentPort;
import huy.project.payment_service.core.usecase.strategy.IPaymentGatewayStrategy;
import huy.project.payment_service.core.usecase.strategy.PaymentGatewayFactory;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CreatePaymentUseCase {
    IPaymentPort paymentPort;
    PaymentGatewayFactory paymentGatewayFactory;

    @Transactional(rollbackOn = Exception.class)
    public PaymentEntity createPayment(Long userId, CreatePaymentDto request) {
        // Check if there's already a COMPLETED payment for this booking
        List<PaymentEntity> completedPayment = paymentPort.getPaymentsByBookingIdAndStatus(
                request.getBookingId(), PaymentStatus.COMPLETED);
        if (!CollectionUtils.isEmpty(completedPayment)) {
            log.error("Payment already completed for bookingId: {}", request.getBookingId());
            throw new AppException(ErrorCode.PAYMENT_ALREADY_COMPLETED);
        }

        // find pending payment
        List<PaymentEntity> pendingPayments = paymentPort.getPaymentsByBookingIdAndStatus(
                request.getBookingId(), PaymentStatus.PENDING
        );
        if (!CollectionUtils.isEmpty(pendingPayments)) {
            pendingPayments.forEach(payment -> payment.setStatus(PaymentStatus.CANCELLED));
            paymentPort.saveAll(pendingPayments);
        }

        // select payment gateway
        String gatewayName = request.getPaymentGateway();
        IPaymentGatewayStrategy paymentGateway;
        if (!StringUtils.hasLength(request.getPaymentGateway())) {
            log.info("Payment gateway not found, using default gateway");
            paymentGateway = paymentGatewayFactory.getDefaultGateway();
        } else {
            paymentGateway = paymentGatewayFactory.getGateway(gatewayName);
        }

        // create payment entity
        PaymentEntity payment = PaymentEntity.builder()
                .bookingId(request.getBookingId())
                .userId(userId)
                .amount(request.getAmount())
                .status(PaymentStatus.PENDING)
                .transactionId(UUID.randomUUID().toString())
                .paymentMethod(request.getPaymentMethod())
                .paymentGateway(paymentGateway.getGatewayName())
                .currency(StringUtils.hasText(request.getCurrency()) ? request.getCurrency() : "VND")
                .build();

        payment = paymentPort.save(payment);

        // process with gateway
        PaymentGatewayResult paymentGatewayResult = paymentGateway.createPayment(request, payment);
        if (!paymentGatewayResult.isSuccess()) {
            payment.setStatus(PaymentStatus.FAILED);
            payment = paymentPort.save(payment);

            log.error("Payment failed: {}", paymentGatewayResult.getMessage());
        }

        // update payment with gateway response
        payment.setPaymentUrl(paymentGatewayResult.getPaymentUrl());
        payment.setGatewayReferenceId(paymentGatewayResult.getGatewayReferenceId());
        payment.setGatewayResponse(paymentGatewayResult.getRawResponse());

        return paymentPort.save(payment);
    }
}
