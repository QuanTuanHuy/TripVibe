package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.PriceTypeModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface IPriceTypeRepository extends IBaseRepository<PriceTypeModel> {
    Optional<PriceTypeModel> findByName(String name);
}
