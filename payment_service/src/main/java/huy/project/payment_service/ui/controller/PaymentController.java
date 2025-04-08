package huy.project.payment_service.ui.controller;

import huy.project.payment_service.core.domain.constant.ErrorCode;
import huy.project.payment_service.core.domain.dto.request.CreatePaymentDto;
import huy.project.payment_service.core.domain.entity.PaymentEntity;
import huy.project.payment_service.core.exception.AppException;
import huy.project.payment_service.core.service.IPaymentService;
import huy.project.payment_service.kernel.utils.AuthenUtils;
import huy.project.payment_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final IPaymentService paymentService;

    @PostMapping
    public ResponseEntity<Resource<PaymentEntity>> createPayment(@RequestBody CreatePaymentDto request) {
        Long userId = AuthenUtils.getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.UN_AUTHORIZED);
        }
        return ResponseEntity.ok(new Resource<>(paymentService.createPayment(userId, request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource<PaymentEntity>> getDetailPayment(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new Resource<>(paymentService.getPaymentById(id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Resource<?>> cancelPayment(@PathVariable Long id) {
        paymentService.cancelPayment(id);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
