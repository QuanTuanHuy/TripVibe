package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.CreateCurrencyDto;
import huy.project.accommodation_service.core.domain.entity.CurrencyEntity;

import java.util.List;

public interface ICurrencyService {
    CurrencyEntity createCurrency(CreateCurrencyDto req);
    List<CurrencyEntity> getCurrencies();
    void deleteCurrencyById(Long id);
}
