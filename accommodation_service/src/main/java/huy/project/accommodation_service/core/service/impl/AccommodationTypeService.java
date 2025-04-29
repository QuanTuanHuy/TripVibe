package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.AccommodationTypeParams;
import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationTypeDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;
import huy.project.accommodation_service.core.service.IAccommodationTypeService;
import huy.project.accommodation_service.core.usecase.CreateAccommodationTypeUseCase;
import huy.project.accommodation_service.core.usecase.GetAccommodationTypeUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccommodationTypeService implements IAccommodationTypeService {
    CreateAccommodationTypeUseCase createAccommodationTypeUseCase;
    GetAccommodationTypeUseCase getAccommodationTypeUseCase;

    @Override
    public AccommodationTypeEntity createAccommodationType(CreateAccommodationTypeDto req) {
        return createAccommodationTypeUseCase.createAccommodationType(req);
    }

    @Override
    public List<AccommodationTypeEntity> getAccommodationTypes(AccommodationTypeParams params) {
        return getAccommodationTypeUseCase.getAccommodationTypes(params);
    }
}
