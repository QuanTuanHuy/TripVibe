package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.BedEntity;
import huy.project.accommodation_service.core.domain.entity.BedTypeEntity;
import huy.project.accommodation_service.core.domain.entity.BedroomEntity;
import huy.project.accommodation_service.core.port.IBedPort;
import huy.project.accommodation_service.core.port.IBedTypePort;
import huy.project.accommodation_service.core.port.IBedroomPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetBedroomUseCase {
    private final IBedroomPort bedroomPort;
    private final IBedPort bedPort;
    private final IBedTypePort bedTypePort;

    public List<BedroomEntity> getBedroomsByUnitIds(List<Long> unitIds) {
        List<BedroomEntity> bedrooms = bedroomPort.getBedroomsByUnitIds(unitIds);
        if (CollectionUtils.isEmpty(bedrooms)) {
            return bedrooms;
        }
        setBedToBedroom(bedrooms);

        return bedrooms;
    }

    public List<BedroomEntity> getBedroomsByUnitId(Long unitId) {
        List<BedroomEntity> bedrooms = bedroomPort.getBedroomsByUnitId(unitId);
        setBedToBedroom(bedrooms);

        return bedrooms;
    }

    private void setBedToBedroom(List<BedroomEntity> bedrooms) {
        List<Long> bedroomIds = bedrooms.stream().map(BedroomEntity::getId).toList();
        List<BedEntity> beds = bedPort.getBedsByBedroomIds(bedroomIds);

        List<Long> bedTypeIds = beds.stream().map(BedEntity::getBedTypeId).distinct().toList();
        var bedTypeMap = bedTypePort.getBedTypesByIds(bedTypeIds).stream()
                .collect(Collectors.toMap(BedTypeEntity::getId, Function.identity()));

        beds.forEach(b -> b.setType(bedTypeMap.get(b.getBedTypeId())));

        var bedGroup = beds.stream().collect(Collectors.groupingBy(BedEntity::getBedroomId));

        bedrooms.forEach(bedroom -> bedroom.setBeds(bedGroup.get(bedroom.getId())));
    }
}
