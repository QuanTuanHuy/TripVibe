package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.port.IAmenityPort;
import huy.project.accommodation_service.infrastructure.repository.IAmenityRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.AmenityMapper;
import huy.project.accommodation_service.infrastructure.repository.model.AmenityModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityAdapter implements IAmenityPort {
    private final IAmenityRepository amenityRepository;

    @Override
    public AmenityEntity save(AmenityEntity amenity) {
        AmenityModel amenityModel = AmenityMapper.INSTANCE.toModel(amenity);
        return AmenityMapper.INSTANCE.toEntity(amenityRepository.save(amenityModel));
    }

    @Override
    public AmenityEntity getAmenityByName(String name) {
        return amenityRepository.findByName(name)
                .map(AmenityMapper.INSTANCE::toEntity).orElse(null);
    }

    @Override
    public List<AmenityEntity> getAmenitiesByGroupId(Long groupId) {
        return AmenityMapper.INSTANCE.toListAmenity(amenityRepository.findByGroupId(groupId));
    }

    @Override
    public List<AmenityEntity> getAmenitiesByGroupIds(List<Long> groupIds) {
        return AmenityMapper.INSTANCE.toListAmenity(amenityRepository.findByGroupIdIn(groupIds));
    }

    @Override
    public AmenityEntity getAmenityById(Long id) {
        return amenityRepository.findById(id)
                .map(AmenityMapper.INSTANCE::toEntity).orElse(null);
    }

    @Override
    public void deleteAmenityById(Long id) {
        amenityRepository.deleteById(id);
    }

    @Override
    public void deleteAmenityByGroupId(Long groupId) {
        amenityRepository.deleteByGroupId(groupId);
    }
}
