package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.UnitPriceCalendarModel;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface IUnitPriceCalendarRepository extends IBaseRepository<UnitPriceCalendarModel> {
    void deleteByUnitIdAndDateBetween(Long unitId, LocalDate start, LocalDate end);
}
