package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.CurrencyModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICurrencyRepository extends IBaseRepository<CurrencyModel> {
    Optional<CurrencyModel> findByCode(String code);
}
