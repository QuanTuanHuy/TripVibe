package huy.project.search_service.core.service;

import huy.project.search_service.core.domain.dto.request.AccommodationParams;
import huy.project.search_service.core.domain.dto.response.AccommodationThumbnail;
import huy.project.search_service.core.domain.dto.response.PageInfo;
import huy.project.search_service.core.domain.entity.AccommodationEntity;
import huy.project.search_service.core.domain.entity.UnitEntity;
import huy.project.search_service.core.usecase.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccommodationService implements IAccommodationService{
    CreateAccommodationUseCase createAccommodationUseCase;
    GetAccommodationUseCase getAccommodationUseCase;
    DeleteAccommodationUseCase deleteAccommodationUseCase;
    AddUnitUseCase addUnitUseCase;
    DeleteUnitUseCase deleteUnitUseCase;
    UpdateAccommodationUseCase updateAccommodationUseCase;

    @Override
    public AccommodationEntity createAccommodation(AccommodationEntity accommodation) {
        return createAccommodationUseCase.createAccommodation(accommodation);
    }

    @Override
    public AccommodationEntity getAccById(Long id) {
        return getAccommodationUseCase.getAccById(id);
    }

    @Override
    public void deleteAccommodation(Long id) {
        deleteAccommodationUseCase.deleteAccommodation(id);
    }

    @Override
    public void addUnitToAccommodation(Long accId, UnitEntity unit) {
        addUnitUseCase.addUnitToAccommodation(accId, unit);
    }

    @Override
    public Pair<PageInfo, List<AccommodationEntity>> getAccommodations(AccommodationParams params) {
        return getAccommodationUseCase.getAccommodations(params);
    }

    @Override
    public Pair<PageInfo, List<AccommodationThumbnail>> getAccommodationsThumbnail(AccommodationParams params) {
        return getAccommodationUseCase.getAccommodationsThumbnail(params);
    }

    @Override
    public void deleteUnit(Long accId, Long unitId) {
        deleteUnitUseCase.deleteUnit(accId, unitId);
    }

    @Override
    public void updateAccommodation(AccommodationEntity accommodation) {
        updateAccommodationUseCase.updateAccommodation(accommodation);
    }
}
