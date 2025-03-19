package huy.project.search_service.core.usecase;

import huy.project.search_service.core.domain.entity.UnitEntity;
import huy.project.search_service.core.port.IAccommodationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddUnitUseCase {
    private final IAccommodationPort accommodationPort;
    private final GetAccommodationUseCase getAccommodationUseCase;

    public void addUnitToAccommodation(Long accId, UnitEntity unit) {
        var accommodation = getAccommodationUseCase.getAccById(accId);

        List<Long> unitIds = accommodation.getUnits().stream().map(UnitEntity::getId).toList();
        if (unitIds.contains(unit.getId())) {
            log.error("unit {} already exists in accommodation {}", unit.getId(), accId);
            return;
        }

        accommodation.getUnits().add(unit);
        accommodationPort.save(accommodation);
    }
}
