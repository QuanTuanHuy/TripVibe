package huy.project.search_service.core.service;

import huy.project.search_service.core.domain.dto.request.AccommodationParams;
import huy.project.search_service.core.domain.dto.response.AccommodationThumbnail;
import huy.project.search_service.core.domain.dto.response.PageInfo;
import huy.project.search_service.core.domain.entity.AccommodationEntity;
import huy.project.search_service.core.domain.entity.UnitEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IAccommodationService {
    AccommodationEntity createAccommodation(AccommodationEntity accommodation);
    AccommodationEntity getAccById(Long id);
    void deleteAccommodation(Long id);
    void addUnitToAccommodation(Long accId, UnitEntity unit);
    Pair<PageInfo, List<AccommodationEntity>> getAccommodations(AccommodationParams params);
    Pair<PageInfo, List<AccommodationThumbnail>> getAccommodationsThumbnail(AccommodationParams params);
    void deleteUnit(Long accId, Long unitId);
    void updateAccommodation(AccommodationEntity accommodation);
}
