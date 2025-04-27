package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.UnitPriceCalendarModel;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IUnitPriceCalendarRepository extends IBaseRepository<UnitPriceCalendarModel> {
    void deleteByUnitIdAndDateBetween(Long unitId, LocalDate start, LocalDate end);

    List<UnitPriceCalendarModel> findByUnitIdAndDateBetween(Long unitId, LocalDate start, LocalDate end);
}
