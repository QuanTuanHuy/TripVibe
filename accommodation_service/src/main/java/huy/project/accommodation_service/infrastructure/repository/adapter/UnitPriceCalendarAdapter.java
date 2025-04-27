package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.UnitPriceCalendarEntity;
import huy.project.accommodation_service.core.port.IUnitPriceCalendarPort;
import huy.project.accommodation_service.infrastructure.repository.IUnitPriceCalendarRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.UnitPriceCalendarMapper;
import huy.project.accommodation_service.infrastructure.repository.model.UnitPriceCalendarModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitPriceCalendarAdapter implements IUnitPriceCalendarPort {
    private final IUnitPriceCalendarRepository unitPriceCalendarRepository;

    @Override
    public void saveAll(List<UnitPriceCalendarEntity> unitPriceCalendars) {
        List<UnitPriceCalendarModel> unitPrices = UnitPriceCalendarMapper.INSTANCE.toListModel(unitPriceCalendars);
        unitPriceCalendarRepository.saveAll(unitPrices);
    }

    @Override
    public void save(UnitPriceCalendarEntity unitPriceCalendar) {
        unitPriceCalendarRepository.save(UnitPriceCalendarMapper.INSTANCE.toModel(unitPriceCalendar));
    }

    @Override
    public void deletePriceByUnitIdAndDate(Long unitId, LocalDate start, LocalDate end) {
        unitPriceCalendarRepository.deleteByUnitIdAndDateBetween(unitId, start, end);
    }

    @Override
    public List<UnitPriceCalendarEntity> getByUnitIdAndDate(Long unitId, LocalDate start, LocalDate end) {
        return UnitPriceCalendarMapper.INSTANCE.toListEntity(
                unitPriceCalendarRepository.findByUnitIdAndDateBetween(unitId, start, end)
        );
    }

}
