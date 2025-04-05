package huy.project.search_service.core.usecase;

import org.springframework.stereotype.Service;

import huy.project.search_service.core.domain.constant.ErrorCode;
import huy.project.search_service.core.domain.exception.AppException;
import huy.project.search_service.core.port.IAccommodationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteUnitUseCase {
    private final IAccommodationPort accommodationPort;

    public void deleteUnit(Long accId, Long unitId) {
        var accommodation = accommodationPort.getAccById(accId);
        if (accommodation == null) {
            log.error("Accommodation with id {} not found", accId);
            throw new AppException(ErrorCode.ACCOMMODATION_NOT_FOUND);
        }

        var deleteUnit = accommodation.getUnits().stream().filter(u -> u.getId().equals(unitId)).findFirst();
        if (deleteUnit.isEmpty()) {
            log.error("Unit with id {} not found", unitId);
            throw new AppException(ErrorCode.UNIT_NOT_FOUND);
        }

        var newUnits = accommodation.getUnits().stream().filter(u -> !u.getId().equals(unitId)).toList();
        accommodation.setUnits(newUnits);

        accommodationPort.save(accommodation);
    }
}
