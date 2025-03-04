package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.AmenityEntity;
import huy.project.accommodation_service.core.domain.entity.UnitAmenityEntity;
import huy.project.accommodation_service.core.port.IUnitAmenityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUnitAmenityUseCase {
    private final IUnitAmenityPort unitAmenityPort;

    private final GetAmenityUseCase getAmenityUseCase;

    public List<UnitAmenityEntity> getUnitAmenitiesByUnitIds(List<Long> unitIds) {
        List<UnitAmenityEntity> unitAmenities = unitAmenityPort.getUnitAmenitiesByUnitIds(unitIds);

        // get amenities
        List<Long> amenityIds = unitAmenities.stream()
                .map(UnitAmenityEntity::getAmenityId).distinct().toList();
        List<AmenityEntity> amenities = getAmenityUseCase.getAmenitiesByIds(amenityIds);
        var amenityMap = amenities.stream().collect(Collectors.toMap(AmenityEntity::getId, Function.identity()));

        return unitAmenities.stream()
                .peek(unitAmenity -> unitAmenity.setAmenity(amenityMap.get(unitAmenity.getAmenityId())))
                .toList();
    }
}
