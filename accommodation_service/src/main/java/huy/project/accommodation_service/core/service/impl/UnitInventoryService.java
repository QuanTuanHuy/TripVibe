package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.UpdateUnitInventoryRequest;
import huy.project.accommodation_service.core.domain.entity.UnitInventoryEntity;
import huy.project.accommodation_service.core.service.IUnitInventoryService;
import huy.project.accommodation_service.core.usecase.GetUnitInventoryUseCase;
import huy.project.accommodation_service.core.usecase.UpdateUnitInventoryUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UnitInventoryService implements IUnitInventoryService {
    UpdateUnitInventoryUseCase updateInventoryUseCase;
    GetUnitInventoryUseCase getUnitInventoryUseCase;

    @Override
    public List<UnitInventoryEntity> getUnitInventoryForUnit(Long unitId, LocalDate startDate, LocalDate endDate) {
        return getUnitInventoryUseCase.getUnitInventoryForUnit(unitId, startDate, endDate);
    }

    @Override
    public List<UnitInventoryEntity> blockInventory(Long userId, Long unitId, LocalDate startDate, LocalDate endDate, String reason) {
        return updateInventoryUseCase.blockInventory(userId, unitId, startDate, endDate, reason);
    }

    @Override
    public List<UnitInventoryEntity> updateInventory(Long userId, Long unitId, List<UpdateUnitInventoryRequest> updates, String source) {
        return updateInventoryUseCase.updateInventory(userId, unitId, updates, source);
    }
}
