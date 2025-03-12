package huy.project.profile_service.infrastructure.repository.mapper;

import huy.project.profile_service.core.domain.entity.CreditCardEntity;
import huy.project.profile_service.infrastructure.repository.model.CreditCardModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class CreditCardMapper {
    public static final CreditCardMapper INSTANCE = Mappers.getMapper(CreditCardMapper.class);

    public abstract CreditCardEntity toEntity(CreditCardModel model);
    public abstract CreditCardModel toModel(CreditCardEntity entity);
}
