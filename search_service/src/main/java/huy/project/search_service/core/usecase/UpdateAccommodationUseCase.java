package huy.project.search_service.core.usecase;

import huy.project.search_service.core.domain.entity.AccommodationEntity;
import huy.project.search_service.core.port.IAccommodationPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateAccommodationUseCase {
    IAccommodationPort accommodationPort;

    public void updateAccommodation(AccommodationEntity accommodation) {
        try {
            accommodationPort.save(accommodation);
        } catch (Exception e) {
            log.error("Error while updating accommodation: {}", e.getMessage());
        }
    }
}
