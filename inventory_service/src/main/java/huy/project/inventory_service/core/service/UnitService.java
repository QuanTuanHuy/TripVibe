package huy.project.inventory_service.core.service;

import huy.project.inventory_service.core.domain.dto.request.SyncUnitDto;
import huy.project.inventory_service.core.domain.entity.Unit;
import huy.project.inventory_service.core.usecase.CreateUnitUseCase;
import huy.project.inventory_service.core.usecase.GetAccommodationUseCase;
import huy.project.inventory_service.core.usecase.GetUnitUseCase;
import huy.project.inventory_service.core.usecase.UpdateUnitUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitService implements IUnitService {
    private final CreateUnitUseCase createUnitUseCase;
    private final GetUnitUseCase getUnitUseCase;
    private final UpdateUnitUseCase updateUnitUseCase;
    private final GetAccommodationUseCase getAccommodationUseCase;

    @Override
    public Unit syncUnit(SyncUnitDto request) {
        log.info("Synchronizing unit inventory: accommodationId={}, unitId={}, name={}, quantity={}",
                request.getAccommodationId(), request.getUnitId(), request.getUnitName(), request.getQuantity());

        getAccommodationUseCase.getAccommodationById(request.getAccommodationId());

        var existingUnit = getUnitUseCase.getUnitById(request.getUnitId());
        if (existingUnit != null) {
            return updateUnitUseCase.updateUnit(request);
        } else {
            return createUnitUseCase.createUnit(request);
        }
    }
}
