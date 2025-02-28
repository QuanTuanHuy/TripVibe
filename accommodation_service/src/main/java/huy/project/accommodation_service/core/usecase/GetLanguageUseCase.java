package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.dto.request.GetLanguageParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import huy.project.accommodation_service.core.port.ILanguagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetLanguageUseCase {
    private final ILanguagePort languagePort;

    public Pair<PageInfo, List<LanguageEntity>> getAllLanguages(GetLanguageParams params) {
        return languagePort.getLanguages(params);
    }
}
