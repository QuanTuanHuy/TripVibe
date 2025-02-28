package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.LanguageEntity;

public interface ILanguagePort {
    LanguageEntity save(LanguageEntity language);
    LanguageEntity getLanguageByName(String name);
    LanguageEntity getLanguageByCode(String code);
}
