package huy.project.search_service.core.usecase;

import huy.project.search_service.core.domain.constant.ErrorCode;
import huy.project.search_service.core.domain.entity.AccommodationEntity;
import huy.project.search_service.core.domain.exception.AppException;
import huy.project.search_service.core.port.IAccommodationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateAccommodationUseCase {
    private final IAccommodationPort accommodationPort;

    public AccommodationEntity createAccommodation(AccommodationEntity accommodation) {
        var existedAcc = accommodationPort.getAccById(accommodation.getId());
        if (existedAcc != null) {
            log.error("Accommodation with id {} already exists", accommodation.getId());
            throw new AppException(ErrorCode.ACCOMMODATION_ALREADY_EXISTS);
        }

        return accommodationPort.save(accommodation);
    }
}
