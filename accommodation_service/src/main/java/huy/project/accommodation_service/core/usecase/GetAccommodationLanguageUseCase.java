package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.AccommodationLanguageEntity;
import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import huy.project.accommodation_service.core.port.IAccommodationLanguagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAccommodationLanguageUseCase {
    private final IAccommodationLanguagePort accLanguagePort;

    private final GetLanguageUseCase getLanguageUseCase;

    public List<LanguageEntity> getLanguageByAccId(Long accId) {
        var accLanguages = accLanguagePort.getAccLanguagesByAccId(accId);

        List<Long> languageIds = accLanguages.stream().map(AccommodationLanguageEntity::getLanguageId).toList();
        return getLanguageUseCase.getLanguagesByIds(languageIds);
    }
}
