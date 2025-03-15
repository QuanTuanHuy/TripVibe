package huy.project.search_service.core.service;

import huy.project.search_service.core.domain.dto.request.AccommodationParams;
import huy.project.search_service.core.domain.dto.response.PageInfo;
import huy.project.search_service.core.domain.entity.AccommodationEntity;
import huy.project.search_service.core.usecase.CreateAccommodationUseCase;
import huy.project.search_service.core.usecase.DeleteAccommodationUseCase;
import huy.project.search_service.core.usecase.GetAccommodationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationService implements IAccommodationService{
    private final CreateAccommodationUseCase createAccommodationUseCase;
    private final GetAccommodationUseCase getAccommodationUseCase;
    private final DeleteAccommodationUseCase deleteAccommodationUseCase;

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
    public Pair<PageInfo, List<AccommodationEntity>> getAccommodation(AccommodationParams params) {
        return getAccommodationUseCase.getAccommodation(params);
    }
}
