package huy.project.inventory_service.core.port;

import huy.project.inventory_service.core.domain.entity.Accommodation;

import java.util.List;

public interface IAccommodationPort {
    Accommodation save(Accommodation accommodation);

    Accommodation getAccommodationById(Long id);

    List<Accommodation> findAll();

    List<Accommodation> saveAll(List<Accommodation> accommodations);

    void deleteAccommodationById(Long id);
}
