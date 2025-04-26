package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import huy.project.accommodation_service.core.domain.entity.UnitPriceCalendarEntity;
import huy.project.accommodation_service.core.port.IUnitPriceCalendarPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetUnitPricingCalendarUseCase {
    IUnitPriceCalendarPort unitPriceCalendarPort;
    GetUnitUseCase getUnitUseCase;

    @Transactional(readOnly = true)
    public List<UnitPriceCalendarEntity> getUnitPricingCalendars(Long unitId, LocalDate startDate, LocalDate endDate) {
        UnitEntity unit = getUnitUseCase.getUnitById(unitId);

        var dates = startDate.datesUntil(endDate.plusDays(1)).toList();

        var priceCalendars = unitPriceCalendarPort.getByUnitIdAndDate(unitId, startDate, endDate);
        if (priceCalendars.size() == dates.size()) {
            return priceCalendars;
        }

        autoFillUnitPriceCalendars(unit, dates, priceCalendars);

        priceCalendars.sort(Comparator.comparing(UnitPriceCalendarEntity::getDate));

        return priceCalendars;
    }

    public void autoFillUnitPriceCalendars(
            UnitEntity unit, List<LocalDate> dates, List<UnitPriceCalendarEntity> priceCalendars) {

        var priceCalendarMap = priceCalendars.stream()
                .collect(Collectors.toMap(UnitPriceCalendarEntity::getDate,
                        priceCalendar -> priceCalendar));

        for (LocalDate date : dates) {
            if (!priceCalendarMap.containsKey(date)) {
                priceCalendars.add(UnitPriceCalendarEntity.newPriceCalendar(unit, date));
            }
        }
    }
}
