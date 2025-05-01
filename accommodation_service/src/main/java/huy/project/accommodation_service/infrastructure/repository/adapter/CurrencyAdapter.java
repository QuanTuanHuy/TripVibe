package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.entity.CurrencyEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.ICurrencyPort;
import huy.project.accommodation_service.infrastructure.repository.ICurrencyRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.CurrencyMapper;
import huy.project.accommodation_service.infrastructure.repository.model.CurrencyModel;
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
public class CurrencyAdapter implements ICurrencyPort {
    ICurrencyRepository currencyRepository;

    @Override
    public CurrencyEntity save(CurrencyEntity currency) {
        try {
            CurrencyModel model = CurrencyMapper.INSTANCE.toModel(currency);
            return CurrencyMapper.INSTANCE.toEntity(currencyRepository.save(model));
        } catch (Exception e) {
            log.error("Error when saving currency: {}", e.getMessage());
            throw new AppException(ErrorCode.SAVE_CURRENCY_FAILED);
        }
    }

    @Override
    public List<CurrencyEntity> getCurrencies() {
        return CurrencyMapper.INSTANCE.toListEntity(currencyRepository.findAll());
    }

    @Override
    public CurrencyEntity getCurrencyById(Long id) {
        return CurrencyMapper.INSTANCE.toEntity(currencyRepository.findById(id).orElse(null));
    }

    @Override
    public CurrencyEntity getCurrencyByCode(String code) {
        return CurrencyMapper.INSTANCE.toEntity(currencyRepository.findByCode(code).orElse(null));
    }

    @Override
    public void deleteById(Long id) {
        currencyRepository.deleteById(id);
    }
}
