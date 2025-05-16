package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.entity.Accommodation;
import huy.project.inventory_service.core.domain.exception.NotFoundException;
import huy.project.inventory_service.core.port.IAccommodationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAccommodationUseCase {
    private final IAccommodationPort accommodationPort;

    public Accommodation getAccommodationById(Long id) {
        var accommodation = accommodationPort.getAccommodationById(id);
        if (accommodation == null) {
            throw new NotFoundException("Accommodation not found, id: " + id);
        }
        return accommodation;
    }
}
