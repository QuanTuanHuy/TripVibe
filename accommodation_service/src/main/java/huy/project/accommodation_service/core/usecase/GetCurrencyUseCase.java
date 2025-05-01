package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.CurrencyEntity;
import huy.project.accommodation_service.core.port.ICurrencyPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetCurrencyUseCase {
    ICurrencyPort currencyPort;

    public List<CurrencyEntity> getCurrencies() {
        return currencyPort.getCurrencies();
    }

    public CurrencyEntity getCurrencyById(Long id) {
        return currencyPort.getCurrencyById(id);
    }
}
