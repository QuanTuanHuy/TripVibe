package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.CreateCurrencyDto;
import huy.project.accommodation_service.core.domain.entity.CurrencyEntity;
import huy.project.accommodation_service.core.service.ICurrencyService;
import huy.project.accommodation_service.core.usecase.CreateCurrencyUseCase;
import huy.project.accommodation_service.core.usecase.DeleteCurrencyUseCase;
import huy.project.accommodation_service.core.usecase.GetCurrencyUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CurrencyService implements ICurrencyService {
    CreateCurrencyUseCase createCurrencyUseCase;
    GetCurrencyUseCase getCurrencyUseCase;
    DeleteCurrencyUseCase deleteCurrencyUseCase;

    @Override
    public CurrencyEntity createCurrency(CreateCurrencyDto req) {
        return createCurrencyUseCase.createCurrency(req);
    }

    @Override
    public List<CurrencyEntity> getCurrencies() {
        return getCurrencyUseCase.getCurrencies();
    }

    @Override
    public void deleteCurrencyById(Long id) {
        deleteCurrencyUseCase.deleteCurrency(id);
    }

    @Override
    public void createIfNotExists(List<CreateCurrencyDto> currencies) {
        createCurrencyUseCase.createIfNotExists(currencies);
    }
}
