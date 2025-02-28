package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.CreateLanguageRequestDto;
import huy.project.accommodation_service.core.domain.entity.LanguageEntity;

public interface ILanguageService {
    LanguageEntity createLanguage(CreateLanguageRequestDto req);
}
