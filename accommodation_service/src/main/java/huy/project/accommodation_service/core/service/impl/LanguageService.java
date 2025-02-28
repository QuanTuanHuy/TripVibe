package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.CreateLanguageRequestDto;
import huy.project.accommodation_service.core.domain.dto.request.GetLanguageParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import huy.project.accommodation_service.core.service.ILanguageService;
import huy.project.accommodation_service.core.usecase.CreateLanguageUseCase;
import huy.project.accommodation_service.core.usecase.GetLanguageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageService implements ILanguageService {
    private final CreateLanguageUseCase createLanguageUseCase;
    private final GetLanguageUseCase getLanguageUseCase;

    @Override
    public LanguageEntity createLanguage(CreateLanguageRequestDto req) {
        return createLanguageUseCase.createLanguage(req);
    }

    @Override
    public Pair<PageInfo, List<LanguageEntity>> getAllLanguages(GetLanguageParams params) {
        return getLanguageUseCase.getAllLanguages(params);
    }
}
