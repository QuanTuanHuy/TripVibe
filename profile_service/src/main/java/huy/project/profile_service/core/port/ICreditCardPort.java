package huy.project.profile_service.core.port;

import huy.project.profile_service.core.domain.entity.CreditCardEntity;

public interface ICreditCardPort {
    CreditCardEntity save(CreditCardEntity creditCard);
    CreditCardEntity getCreditCardById(Long id);
}
