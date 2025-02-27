package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.AmenityGroupEntity;
import huy.project.accommodation_service.core.port.IAmenityGroupPort;
import huy.project.accommodation_service.infrastructure.repository.IAmenityGroupRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.AmenityGroupMapper;
import huy.project.accommodation_service.infrastructure.repository.model.AmenityGroupModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmenityGroupAdapter implements IAmenityGroupPort {
    private final IAmenityGroupRepository amenityGroupRepository;

    @Override
    public AmenityGroupEntity save(AmenityGroupEntity amenityGroup) {
        AmenityGroupModel amenityGroupModel = AmenityGroupMapper.INSTANCE.toModel(amenityGroup);
        return AmenityGroupMapper.INSTANCE.toEntity(amenityGroupRepository.save(amenityGroupModel));
    }

    @Override
    public AmenityGroupEntity getAmenityGroupById(Long id) {
        return AmenityGroupMapper.INSTANCE.toEntity(amenityGroupRepository.findById(id).orElse(null));
    }

    @Override
    public AmenityGroupEntity getAmenityGroupByName(String name) {
        return amenityGroupRepository.findByName(name)
                .map(AmenityGroupMapper.INSTANCE::toEntity)
                .orElse(null);
    }
}
