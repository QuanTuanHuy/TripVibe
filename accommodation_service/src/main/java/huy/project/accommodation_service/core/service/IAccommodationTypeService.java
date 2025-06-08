package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.AccommodationTypeParams;
import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationTypeDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationTypeEntity;

import java.util.List;

public interface IAccommodationTypeService {
    AccommodationTypeEntity createAccommodationType(CreateAccommodationTypeDto req);

    List<AccommodationTypeEntity> getAccommodationTypes(AccommodationTypeParams params);

    void createIfNotExists(List<CreateAccommodationTypeDto> accommodationTypes);
}
