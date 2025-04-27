package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.UnitInventoryModel;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IUnitInventoryRepository extends IBaseRepository<UnitInventoryModel> {
    List<UnitInventoryModel> findByUnitIdAndDateBetween(Long unitId, LocalDate startDate, LocalDate endDate);
    Optional<UnitInventoryModel> findByUnitIdAndDate(Long unitId, LocalDate date);
}
