package huy.project.inventory_service.core.service.impl;

import huy.project.inventory_service.core.domain.dto.request.SyncAccommodationDto;
import huy.project.inventory_service.core.domain.entity.Accommodation;
import huy.project.inventory_service.core.service.IAccommodationService;
import huy.project.inventory_service.core.service.IUnitService;
import huy.project.inventory_service.core.usecase.CreateOrUpdateAccUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccommodationService implements IAccommodationService {
    private final CreateOrUpdateAccUseCase createOrUpdateAccUseCase;
    private final IUnitService unitService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Accommodation syncAccommodation(SyncAccommodationDto request) {
        var accommodation = createOrUpdateAccUseCase.createOrUpdate(request);
        var units = request.getUnits().stream()
                .map(unitService::syncUnit)
                .toList();
        accommodation.setUnits(units);
        return accommodation;
    }
}
