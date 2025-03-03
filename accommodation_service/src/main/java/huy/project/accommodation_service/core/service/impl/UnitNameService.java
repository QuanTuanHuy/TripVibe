package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.CreateUnitNameDto;
import huy.project.accommodation_service.core.domain.dto.request.UnitNameParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.UnitNameEntity;
import huy.project.accommodation_service.core.service.IUnitNameService;
import huy.project.accommodation_service.core.usecase.CreateUnitNameUseCase;
import huy.project.accommodation_service.core.usecase.GetUnitNameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitNameService implements IUnitNameService {
    private final CreateUnitNameUseCase createUnitNameUseCase;
    private final GetUnitNameUseCase getUnitNameUseCase;

    @Override
    public UnitNameEntity createUnitName(CreateUnitNameDto req) {
        return createUnitNameUseCase.createUnitName(req);
    }

    @Override
    public Pair<PageInfo, List<UnitNameEntity>> getUnitNames(UnitNameParams params) {
        return getUnitNameUseCase.getUnitNames(params);
    }
}
