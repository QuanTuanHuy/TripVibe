package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.service.IAccommodationService;
import huy.project.accommodation_service.core.usecase.CreateAccommodationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationService implements IAccommodationService {
    private final CreateAccommodationUseCase createAccommodationUseCase;

    @Override
    public AccommodationEntity createAccommodation(Long userId, CreateAccommodationDto req) {
        return createAccommodationUseCase.createAccommodation(userId, req);
    }
}
