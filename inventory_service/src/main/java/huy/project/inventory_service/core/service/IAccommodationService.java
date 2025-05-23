package huy.project.inventory_service.core.service;

import huy.project.inventory_service.core.domain.dto.request.SyncAccommodationDto;
import huy.project.inventory_service.core.domain.entity.Accommodation;

public interface IAccommodationService {
    Accommodation syncAccommodation(SyncAccommodationDto request);
}
