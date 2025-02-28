package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import huy.project.accommodation_service.core.port.ILanguagePort;
import huy.project.accommodation_service.infrastructure.repository.ILanguageRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.LanguageMapper;
import huy.project.accommodation_service.infrastructure.repository.model.LanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LanguageAdapter implements ILanguagePort {
    private final ILanguageRepository languageRepository;

    @Override
    public LanguageEntity save(LanguageEntity language) {
        LanguageModel languageModel = LanguageMapper.INSTANCE.toModel(language);
        return LanguageMapper.INSTANCE.toEntity(languageRepository.save(languageModel));
    }

    @Override
    public LanguageEntity getLanguageByName(String name) {
        return languageRepository.findByName(name)
                .map(LanguageMapper.INSTANCE::toEntity).orElse(null);
    }

    @Override
    public LanguageEntity getLanguageByCode(String code) {
        return languageRepository.findByCode(code)
                .map(LanguageMapper.INSTANCE::toEntity).orElse(null);
    }
}
