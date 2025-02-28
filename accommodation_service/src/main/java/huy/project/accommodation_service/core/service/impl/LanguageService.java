package huy.project.accommodation_service.core.service.impl;

import huy.project.accommodation_service.core.domain.dto.request.CreateLanguageRequestDto;
import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import huy.project.accommodation_service.core.service.ILanguageService;
import huy.project.accommodation_service.core.usecase.CreateLanguageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LanguageService implements ILanguageService {
    private final CreateLanguageUseCase createLanguageUseCase;

    @Override
    public LanguageEntity createLanguage(CreateLanguageRequestDto req) {
        return createLanguageUseCase.createLanguage(req);
    }
}
