package huy.project.search_service.core.service;

import huy.project.search_service.core.domain.dto.request.AccommodationParams;
import huy.project.search_service.core.domain.dto.response.PageInfo;
import huy.project.search_service.core.domain.entity.AccommodationEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IAccommodationService {
    AccommodationEntity createAccommodation(AccommodationEntity accommodation);
    AccommodationEntity getAccById(Long id);
    void deleteAccommodation(Long id);
    Pair<PageInfo, List<AccommodationEntity>> getAccommodation(AccommodationParams params);
}
