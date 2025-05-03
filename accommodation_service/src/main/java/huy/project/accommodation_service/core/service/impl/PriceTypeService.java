package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.CreatePriceTypeDto;
import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;
import huy.project.accommodation_service.core.service.IPriceTypeService;
import huy.project.accommodation_service.core.usecase.CreatePriceTypeUseCase;
import huy.project.accommodation_service.core.usecase.GetPriceTypeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceTypeService implements IPriceTypeService {
    private final GetPriceTypeUseCase getPriceTypeUseCase;
    private final CreatePriceTypeUseCase createPriceTypeUseCase;

    @Override
    public List<PriceTypeEntity> getALlPriceTypes() {
        return getPriceTypeUseCase.getAllPriceTypes();
    }

    @Override
    public PriceTypeEntity createPriceType(CreatePriceTypeDto req) {
        return createPriceTypeUseCase.createPriceType(req);
    }
}
