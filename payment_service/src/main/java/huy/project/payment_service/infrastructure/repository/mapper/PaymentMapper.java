package huy.project.payment_service.infrastructure.repository.mapper;

import huy.project.payment_service.core.domain.entity.PaymentEntity;
import huy.project.payment_service.infrastructure.repository.model.PaymentModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

@Mapper
public abstract class PaymentMapper {
    public static final PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "fromInstantToLong")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "fromInstantToLong")
    @Mapping(target = "paidAt", source = "paidAt", qualifiedByName = "fromInstantToLong")
    public abstract PaymentEntity toEntity(PaymentModel model);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "fromLongToInstant")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "fromLongToInstant")
    @Mapping(target = "paidAt", source = "paidAt", qualifiedByName = "fromLongToInstant")
    public abstract PaymentModel toModel(PaymentEntity entity);

    public abstract List<PaymentModel> toListModel(List<PaymentEntity> payments);

    public abstract List<PaymentEntity> toListEntity(List<PaymentModel> payments);

    @Named("fromInstantToLong")
    public Long fromInstantToLong(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.toEpochMilli();
    }

    @Named("fromLongToInstant")
    public Instant fromLongToInstant(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochMilli(timestamp);
    }
}
