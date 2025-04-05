package huy.project.profile_service.infrastructure.repository.adapter;

import huy.project.profile_service.core.domain.entity.CreditCardEntity;
import huy.project.profile_service.core.port.ICreditCardPort;
import huy.project.profile_service.infrastructure.repository.ICreditCardRepository;
import huy.project.profile_service.infrastructure.repository.mapper.CreditCardMapper;
import huy.project.profile_service.infrastructure.repository.model.CreditCardModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditCardAdapter implements ICreditCardPort {
    private final ICreditCardRepository creditCardRepository;

    @Override
    public CreditCardEntity save(CreditCardEntity creditCard) {
        CreditCardModel creditCardModel = CreditCardMapper.INSTANCE.toModel(creditCard);
        return CreditCardMapper.INSTANCE.toEntity(creditCardRepository.save(creditCardModel));
    }

    @Override
    public CreditCardEntity getCreditCardById(Long id) {
        return CreditCardMapper.INSTANCE.toEntity(creditCardRepository.findById(id).orElse(null));
    }
}
