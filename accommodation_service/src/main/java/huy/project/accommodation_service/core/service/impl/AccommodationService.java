package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.*;
import huy.project.accommodation_service.core.domain.entity.AccommodationEntity;
import huy.project.accommodation_service.core.service.IAccommodationService;
import huy.project.accommodation_service.core.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationService implements IAccommodationService {
    private final CreateAccommodationUseCase createAccommodationUseCase;
    private final GetAccommodationUseCase getAccommodationUseCase;
    private final UpdateAccommodationUseCase updateAccommodationUseCase;
    private final UpdateUnitUseCase updateUnitUseCase;
    private final AddUnitUseCase addUnitUseCase;

    @Override
    public AccommodationEntity createAccommodation(Long userId, CreateAccommodationDto req) {
        return createAccommodationUseCase.createAccommodation(userId, req);
    }

    @Override
    public AccommodationEntity getDetailAccommodation(Long id) {
        return getAccommodationUseCase.getDetailAccommodation(id);
    }

    @Override
    public void addUnitToAccommodation(Long userId, Long accId, CreateUnitDto req) {
        addUnitUseCase.addUnit(userId, accId, req);
    }

    @Override
    public void updateUnitImage(Long userId, Long accId, Long unitId, UpdateUnitImageDto req) {
        updateUnitUseCase.updateUnitImage(userId, accId, unitId, req);
    }

    @Override
    public void updateAccAmenity(Long userId, Long accId, UpdateAccAmenityDto req) {
        updateAccommodationUseCase.updateAccAmenity(userId, accId, req);
    }

    @Override
    public void updateUnitAmenity(Long userId, Long accId, Long unitId, UpdateUnitAmenityDto req) {
        updateUnitUseCase.updateUnitAmenity(userId, accId, unitId, req);
    }
}
