package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.port.IAmenityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAmenityUseCase {
    private final IAmenityPort amenityPort;

    public List<AmenityEntity> getAmenitiesByGroupId(Long groupId) {
        return amenityPort.getAmenitiesByGroupId(groupId);
    }

    public List<AmenityEntity> getAmenitiesByGroupIds(List<Long> groupIds) {
        return amenityPort.getAmenitiesByGroupIds(groupIds);
    }

    public List<AmenityEntity> getAmenitiesByIds(List<Long> ids) {
        return amenityPort.getAmenitiesByIds(ids);
    }
}
