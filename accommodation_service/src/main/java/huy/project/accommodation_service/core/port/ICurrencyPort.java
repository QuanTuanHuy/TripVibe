package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.CurrencyEntity;

import java.util.List;

public interface ICurrencyPort {
    CurrencyEntity save(CurrencyEntity currency);
    List<CurrencyEntity> getCurrencies();
    CurrencyEntity getCurrencyById(Long id);
    CurrencyEntity getCurrencyByCode(String code);
    void deleteById(Long id);
    long countAll();
}