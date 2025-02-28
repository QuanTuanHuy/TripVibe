package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.CreateLanguageRequestDto;
import huy.project.accommodation_service.core.domain.dto.request.GetLanguageParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface ILanguageService {
    LanguageEntity createLanguage(CreateLanguageRequestDto req);
    Pair<PageInfo, List<LanguageEntity>> getAllLanguages(GetLanguageParams params);
    void deleteLanguage(Long id);
}
