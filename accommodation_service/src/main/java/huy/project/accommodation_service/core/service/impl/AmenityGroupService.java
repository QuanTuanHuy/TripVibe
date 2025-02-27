package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.core.service.IAmenityGroupService;
import huy.project.accommodation_service.core.usecase.CreateAmenityGroupUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmenityGroupService implements IAmenityGroupService {
    private final CreateAmenityGroupUseCase createAmenityGroupUseCase;

    @Override
    public AmenityGroupEntity createAmenityGroup(CreateAmenityGroupRequestDto req) {
        return createAmenityGroupUseCase.createAmenityGroup(req);
    }
}
