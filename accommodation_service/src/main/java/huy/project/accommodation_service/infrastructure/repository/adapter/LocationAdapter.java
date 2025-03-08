package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.LocationEntity;
import huy.project.accommodation_service.core.port.ILocationPort;
import huy.project.accommodation_service.infrastructure.repository.ILocationRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.LocationMapper;
import huy.project.accommodation_service.infrastructure.repository.model.LocationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationAdapter implements ILocationPort {
    private final ILocationRepository locationRepository;

    @Override
    public LocationEntity save(LocationEntity location) {
        LocationModel locationModel = LocationMapper.INSTANCE.toModel(location);
        return LocationMapper.INSTANCE.toEntity(locationRepository.save(locationModel));
    }

    @Override
    public LocationEntity getLocationById(Long id) {
        return locationRepository.findById(id).map(LocationMapper.INSTANCE::toEntity).orElse(null);
    }

    @Override
    public void deleteLocationById(Long id) {

    }
}
