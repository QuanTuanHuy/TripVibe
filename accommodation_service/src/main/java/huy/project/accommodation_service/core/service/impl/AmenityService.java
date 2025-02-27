package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.UpdateAmenityRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityRequestDto;
import huy.project.accommodation_service.core.service.IAmenityService;
import huy.project.accommodation_service.core.usecase.CreateAmenityUseCase;
import huy.project.accommodation_service.core.usecase.DeleteAmenityUseCase;
import huy.project.accommodation_service.core.usecase.UpdateAmenityUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmenityService implements IAmenityService {
    private final CreateAmenityUseCase createAmenityUseCase;
    private final UpdateAmenityUseCase updateAmenityUseCase;
    private final DeleteAmenityUseCase deleteAmenityUseCase;

    @Override
    public AmenityEntity createAmenity(CreateAmenityRequestDto req) {
        return createAmenityUseCase.createAmenity(req);
    }

    @Override
    public AmenityEntity updateAmenity(Long id, UpdateAmenityRequestDto req) {
        return updateAmenityUseCase.updateAmenity(id, req);
    }

    @Override
    public void deleteAmenity(Long id) {
        deleteAmenityUseCase.deleteAmenityById(id);
    }
}
