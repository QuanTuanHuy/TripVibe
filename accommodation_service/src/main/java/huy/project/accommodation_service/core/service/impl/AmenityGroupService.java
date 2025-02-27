package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.core.service.IAmenityGroupService;
import huy.project.accommodation_service.core.usecase.CreateAmenityGroupUseCase;
import huy.project.accommodation_service.core.usecase.GetAmenityGroupUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityGroupService implements IAmenityGroupService {
    private final CreateAmenityGroupUseCase createAmenityGroupUseCase;
    private final GetAmenityGroupUseCase getAmenityGroupUseCase;

    @Override
    public AmenityGroupEntity createAmenityGroup(CreateAmenityGroupRequestDto req) {
        return createAmenityGroupUseCase.createAmenityGroup(req);
    }

    @Override
    public AmenityGroupEntity getAmenityGroupById(Long id) {
        return getAmenityGroupUseCase.getAmenityGroupById(id);
    }

    @Override
    public List<AmenityGroupEntity> getAllAmenityGroups() {
        return getAmenityGroupUseCase.getAllAmenityGroups();
    }
}
