package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.entity.UnitEntity;
import huy.project.accommodation_service.core.service.IUnitService;
import huy.project.accommodation_service.core.usecase.GetUnitUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService implements IUnitService {
    private final GetUnitUseCase getUnitUseCase;

    @Override
    public List<UnitEntity> getUnitsByIds(List<Long> ids) {
        return getUnitUseCase.getUnitsByIds(ids);
    }
}
