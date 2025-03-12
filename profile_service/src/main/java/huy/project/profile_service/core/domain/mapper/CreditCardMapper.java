package huy.project.profile_service.core.domain.mapper;

import huy.project.profile_service.core.domain.dto.request.CreateCreditCardDto;
import huy.project.profile_service.core.domain.entity.CreditCardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class CreditCardMapper {
    public static final CreditCardMapper INSTANCE = Mappers.getMapper(CreditCardMapper.class);

    public abstract CreditCardEntity toEntity(CreateCreditCardDto req);
}
