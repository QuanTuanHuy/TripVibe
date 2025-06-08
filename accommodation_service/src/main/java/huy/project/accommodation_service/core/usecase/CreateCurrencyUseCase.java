package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreateCurrencyDto;
import huy.project.accommodation_service.core.domain.entity.CurrencyEntity;
import huy.project.accommodation_service.core.domain.mapper.CurrencyMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.ICurrencyPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CreateCurrencyUseCase {
    ICurrencyPort currencyPort;

    @Transactional(rollbackFor = Exception.class)
    public CurrencyEntity createCurrency(CreateCurrencyDto req) {
        CurrencyEntity existedCurrency = currencyPort.getCurrencyByCode(req.getCode());
        if (existedCurrency != null) {
            throw new AppException(ErrorCode.CURRENCY_ALREADY_EXISTED);
        }

        CurrencyEntity currency = CurrencyMapper.INSTANCE.toEntity(req);
        return currencyPort.save(currency);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createIfNotExists(List<CreateCurrencyDto> currencies) {
        if (currencyPort.countAll() > 0) {
            log.info("Currencies already exist, skipping creation.");
            return;
        }

        currencies.forEach(currency -> {
            try {
                createCurrency(currency);
            } catch (Exception e) {
                log.warn("Failed to create currency: {}, error: {}", currency.getCode(), e.getMessage());
            }
        });
    }
}
