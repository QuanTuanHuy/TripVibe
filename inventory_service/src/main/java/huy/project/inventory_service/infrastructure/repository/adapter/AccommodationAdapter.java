package huy.project.inventory_service.infrastructure.repository.adapter;

import huy.project.inventory_service.core.domain.entity.Accommodation;
import huy.project.inventory_service.core.port.IAccommodationPort;
import org.springframework.stereotype.Component;

@Component
public class AccommodationAdapter implements IAccommodationPort {
    @Override
    public Accommodation save(Accommodation accommodation) {
        return null;
    }

    @Override
    public Accommodation getAccommodationById(Long id) {
        return null;
    }
}
