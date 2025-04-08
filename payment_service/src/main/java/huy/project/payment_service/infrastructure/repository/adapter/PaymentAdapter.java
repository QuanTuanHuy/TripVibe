package huy.project.payment_service.infrastructure.repository.adapter;

import huy.project.payment_service.core.domain.constant.ErrorCode;
import huy.project.payment_service.core.domain.entity.PaymentEntity;
import huy.project.payment_service.core.domain.enums.PaymentStatus;
import huy.project.payment_service.core.exception.AppException;
import huy.project.payment_service.core.port.IPaymentPort;
import huy.project.payment_service.infrastructure.repository.IPaymentRepository;
import huy.project.payment_service.infrastructure.repository.mapper.PaymentMapper;
import huy.project.payment_service.infrastructure.repository.model.PaymentModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentAdapter implements IPaymentPort {
    IPaymentRepository paymentRepository;

    @Override
    public PaymentEntity save(PaymentEntity payment) {
        try {
            PaymentModel paymentModel = PaymentMapper.INSTANCE.toModel(payment);
            return PaymentMapper.INSTANCE.toEntity(paymentRepository.save(paymentModel));
        } catch (Exception e) {
            log.error("Error saving payment: {}", e.getMessage());
            throw new AppException(ErrorCode.SAVE_PAYMENT_FAILED);
        }
    }

    @Override
    public List<PaymentEntity> saveAll(List<PaymentEntity> payments) {
        try {
            List<PaymentModel> paymentModels = PaymentMapper.INSTANCE.toListModel(payments);
            return PaymentMapper.INSTANCE.toListEntity(paymentRepository.saveAll(paymentModels));
        } catch (Exception e) {
            log.error("Error saving payments: {}", e.getMessage());
            throw new AppException(ErrorCode.SAVE_PAYMENT_FAILED);
        }
    }

    @Override
    public List<PaymentEntity> getPaymentsByBookingIdAndStatus(Long bookingId, PaymentStatus status) {
        return PaymentMapper.INSTANCE.toListEntity(paymentRepository.findByBookingIdAndStatus(bookingId, status));
    }

    @Override
    public PaymentEntity getPaymentById(Long id) {
        return PaymentMapper.INSTANCE.toEntity(paymentRepository.findById(id).orElse(null));
    }

    @Override
    public List<PaymentEntity> getPaymentsByBookingId(Long bookingId) {
        return PaymentMapper.INSTANCE.toListEntity(paymentRepository.findByBookingId(bookingId));
    }
}
