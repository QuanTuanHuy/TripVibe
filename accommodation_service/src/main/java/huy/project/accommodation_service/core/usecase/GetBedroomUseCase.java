package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.BedEntity;
import huy.project.accommodation_service.core.domain.entity.BedroomEntity;
import huy.project.accommodation_service.core.port.IBedPort;
import huy.project.accommodation_service.core.port.IBedroomPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetBedroomUseCase {
    private final IBedroomPort bedroomPort;
    private final IBedPort bedPort;

    public List<BedroomEntity> getBedroomsByUnitIds(List<Long> unitIds) {
        List<BedroomEntity> bedrooms = bedroomPort.getBedroomsByUnitIds(unitIds);

        List<Long> bedroomIds = bedrooms.stream().map(BedroomEntity::getId).toList();
        List<BedEntity> beds = bedPort.getBedsByBedroomIds(bedroomIds);
        var bedGroup = beds.stream().collect(Collectors.groupingBy(BedEntity::getBedroomId));

        return bedrooms.stream()
                .peek(bedroom -> bedroom.setBeds(bedGroup.get(bedroom.getId())))
                .toList();
    }
}
