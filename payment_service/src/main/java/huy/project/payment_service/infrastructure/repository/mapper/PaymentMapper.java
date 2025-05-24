package huy.project.payment_service.infrastructure.repository.mapper;

import huy.project.payment_service.core.domain.entity.PaymentEntity;
import huy.project.payment_service.infrastructure.repository.model.PaymentModel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentMapper {
    public PaymentEntity toEntity(PaymentModel model) {
        if (model == null) {
            return null;
        }
        
        PaymentEntity entity = new PaymentEntity();
        entity.setId(model.getId());
        entity.setBookingId(model.getBookingId());
        entity.setUserId(model.getUserId());
        entity.setAmount(model.getAmount());
        entity.setCurrency(model.getCurrency());
        entity.setStatus(model.getStatus());
        entity.setPaymentMethod(model.getPaymentMethod());
        entity.setTransactionId(model.getTransactionId());
        entity.setGatewayReferenceId(model.getGatewayReferenceId());
        entity.setPaymentGateway(model.getPaymentGateway());
        entity.setPaymentUrl(model.getPaymentUrl());
        entity.setGatewayResponse(model.getGatewayResponse());
        entity.setCreatedAt(fromInstantToLong(model.getCreatedAt()));
        entity.setUpdatedAt(fromInstantToLong(model.getUpdatedAt()));
        entity.setPaidAt(fromInstantToLong(model.getPaidAt()));
        
        return entity;
    }

    public PaymentModel toModel(PaymentEntity entity) {
        if (entity == null) {
            return null;
        }
        
        PaymentModel model = new PaymentModel();
        model.setId(entity.getId());
        model.setBookingId(entity.getBookingId());
        model.setUserId(entity.getUserId());
        model.setAmount(entity.getAmount());
        model.setCurrency(entity.getCurrency());
        model.setStatus(entity.getStatus());
        model.setTransactionId(entity.getTransactionId());
        model.setGatewayReferenceId(entity.getGatewayReferenceId());
        model.setPaymentMethod(entity.getPaymentMethod());
        model.setPaymentGateway(entity.getPaymentGateway());
        model.setPaymentUrl(entity.getPaymentUrl());
        model.setGatewayResponse(entity.getGatewayResponse());
        model.setCreatedAt(fromLongToInstant(entity.getCreatedAt()));
        model.setUpdatedAt(fromLongToInstant(entity.getUpdatedAt()));
        model.setPaidAt(fromLongToInstant(entity.getPaidAt()));
        
        return model;
    }

    public List<PaymentModel> toListModel(List<PaymentEntity> payments) {
        if (payments == null) {
            return null;
        }
        
        List<PaymentModel> modelList = new ArrayList<>(payments.size());
        for (PaymentEntity entity : payments) {
            modelList.add(toModel(entity));
        }
        
        return modelList;
    }

    public List<PaymentEntity> toListEntity(List<PaymentModel> payments) {
        if (payments == null) {
            return null;
        }
        
        List<PaymentEntity> entityList = new ArrayList<>(payments.size());
        for (PaymentModel model : payments) {
            entityList.add(toEntity(model));
        }
        
        return entityList;
    }

    public Long fromInstantToLong(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.toEpochMilli();
    }

    public Instant fromLongToInstant(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochMilli(timestamp);
    }
}
