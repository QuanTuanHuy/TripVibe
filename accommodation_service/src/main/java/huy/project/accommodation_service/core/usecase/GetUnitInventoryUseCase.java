package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import huy.project.accommodation_service.core.domain.entity.UnitInventoryEntity;
import huy.project.accommodation_service.core.port.IUnitInventoryPort;
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
public class GetUnitInventoryUseCase {
    IUnitInventoryPort unitInventoryPort;
    GetUnitUseCase getUnitUseCase;

    @Transactional(readOnly = true)
    public List<UnitInventoryEntity> getUnitInventoryForUnit(Long unitId, LocalDate startDate, LocalDate endDate) {
        List<UnitInventoryEntity> unitInventories = unitInventoryPort.getInventoriesByUnitIdAndDateRange(unitId, startDate, endDate);
        var unitInventoryMap = unitInventories.stream()
                .collect(Collectors.toMap(UnitInventoryEntity::getDate, unitInventory -> unitInventory));

        UnitEntity unit = getUnitUseCase.getUnitById(unitId);

        List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).toList();
        for (var date : dates) {
            if (!unitInventoryMap.containsKey(date)) {
                unitInventories.add(UnitInventoryEntity.newInventory(unit, date));
            }
        }

        unitInventories.sort(Comparator.comparing(UnitInventoryEntity::getDate));

        return unitInventories;
    }
}
