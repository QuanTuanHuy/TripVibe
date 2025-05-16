package huy.project.inventory_service.core.port;

import huy.project.inventory_service.core.domain.entity.Accommodation;

public interface IAccommodationPort {
    Accommodation save(Accommodation accommodation);

    Accommodation getAccommodationById(Long id);
}
