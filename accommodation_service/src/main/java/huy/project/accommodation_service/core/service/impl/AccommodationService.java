package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateAccAmenityDto;
import huy.project.accommodation_service.core.domain.dto.request.UpdateUnitImageDto;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.service.IAccommodationService;
import huy.project.accommodation_service.core.usecase.CreateAccommodationUseCase;
import huy.project.accommodation_service.core.usecase.GetAccommodationUseCase;
import huy.project.accommodation_service.core.usecase.UpdateAccommodationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationService implements IAccommodationService {
    private final CreateAccommodationUseCase createAccommodationUseCase;
    private final GetAccommodationUseCase getAccommodationUseCase;
    private final UpdateAccommodationUseCase updateAccommodationUseCase;

    @Override
    public AccommodationEntity createAccommodation(Long userId, CreateAccommodationDto req) {
        return createAccommodationUseCase.createAccommodation(userId, req);
    }

    @Override
    public AccommodationEntity getDetailAccommodation(Long id) {
        return getAccommodationUseCase.getDetailAccommodation(id);
    }

    @Override
    public void deleteAccommodation(Long id) {

    }

    @Override
    public void updateUnitImage(Long userId, Long accId, Long unitId, UpdateUnitImageDto req) {
        updateAccommodationUseCase.updateUnitImage(userId, accId, unitId, req);
    }

    @Override
    public void updateAccAmenity(Long userId, Long accId, UpdateAccAmenityDto req) {
        updateAccommodationUseCase.updateAccAmenity(userId, accId, req);
    }
}
