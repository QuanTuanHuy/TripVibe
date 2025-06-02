package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.BedTypeParams;
import huy.project.accommodation_service.core.domain.dto.request.CreateBedTypeDto;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.BedTypeEntity;
import huy.project.accommodation_service.core.service.IBedTypeService;
import huy.project.accommodation_service.core.usecase.CreateBedTypeUseCase;
import huy.project.accommodation_service.core.usecase.GetBedTypeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BedTypeService implements IBedTypeService {
    private final CreateBedTypeUseCase createBedTypeUseCase;
    private final GetBedTypeUseCase getBedTypeUseCase;

    @Override
    public BedTypeEntity createBedType(CreateBedTypeDto req) {
        return createBedTypeUseCase.createBedType(req);
    }

    @Override
    public Pair<PageInfo, List<BedTypeEntity>> getBedTypes(BedTypeParams params) {
        return getBedTypeUseCase.getBedTypes(params);
    }

    @Override
    public void createIfNotExists(List<CreateBedTypeDto> bedTypes) {
        createBedTypeUseCase.createIfNotExists(bedTypes);
    }
}
