package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.AccommodationLanguageEntity;

import java.util.List;

public interface IAccommodationLanguagePort {
    List<AccommodationLanguageEntity> saveAll(List<AccommodationLanguageEntity> accLanguages);
    List<AccommodationLanguageEntity> getAccLanguagesByAccId(Long accId);
    void deleteAccLanguagesByAccId(Long accId);
}
