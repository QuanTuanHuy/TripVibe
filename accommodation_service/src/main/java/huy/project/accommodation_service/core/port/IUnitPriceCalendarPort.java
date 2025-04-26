package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.UnitPriceCalendarEntity;

import java.time.LocalDate;
import java.util.List;

public interface IUnitPriceCalendarPort {
    void saveAll(List<UnitPriceCalendarEntity> unitPriceCalendars);
    void save(UnitPriceCalendarEntity unitPriceCalendar);
    void deletePriceByUnitIdAndDate(Long unitId, LocalDate start, LocalDate end);
    List<UnitPriceCalendarEntity> getByUnitIdAndDate(Long unitId, LocalDate start, LocalDate end);
}
