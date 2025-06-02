package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.dto.request.GetLanguageParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface ILanguagePort {
    LanguageEntity save(LanguageEntity language);
    LanguageEntity getLanguageByName(String name);
    LanguageEntity getLanguageByCode(String code);
    Pair<PageInfo, List<LanguageEntity>> getLanguages(GetLanguageParams params);
    void deleteLanguageById(Long id);
    LanguageEntity getLanguageById(Long id);
    List<LanguageEntity> getLanguagesByIds(List<Long> ids);
    long countAll();
}
