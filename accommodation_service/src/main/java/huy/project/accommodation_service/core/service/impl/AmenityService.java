package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.entity.CreateAmenityRequestDto;
import huy.project.accommodation_service.core.service.IAmenityService;
import huy.project.accommodation_service.core.usecase.CreateAmenityUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmenityService implements IAmenityService {
    private final CreateAmenityUseCase createAmenityUseCase;

    @Override
    public AmenityEntity createAmenity(CreateAmenityRequestDto req) {
        return createAmenityUseCase.createAmenity(req);
    }
}
