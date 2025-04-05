package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationTypeDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;
import huy.project.accommodation_service.core.service.IAccommodationTypeService;
import huy.project.accommodation_service.core.usecase.CreateAccommodationTypeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationTypeService implements IAccommodationTypeService {
    private final CreateAccommodationTypeUseCase createAccommodationTypeUseCase;

    @Override
    public AccommodationTypeEntity createAccommodationType(CreateAccommodationTypeDto req) {
        return createAccommodationTypeUseCase.createAccommodationType(req);
    }
}
