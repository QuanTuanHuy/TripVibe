package huy.project.profile_service.infrastructure.repository.adapter;

import huy.project.profile_service.core.domain.entity.LocationEntity;
import huy.project.profile_service.core.port.ILocationPort;
import huy.project.profile_service.infrastructure.repository.ILocationRepository;
import huy.project.profile_service.infrastructure.repository.mapper.LocationMapper;
import huy.project.profile_service.infrastructure.repository.model.LocationModel;
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
    public void deleteLocationById(Long id) {
        locationRepository.deleteById(id);
    }
}
