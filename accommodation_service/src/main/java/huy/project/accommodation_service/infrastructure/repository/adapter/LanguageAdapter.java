package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.dto.request.GetLanguageParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import huy.project.accommodation_service.core.port.ILanguagePort;
import huy.project.accommodation_service.infrastructure.repository.ILanguageRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.LanguageMapper;
import huy.project.accommodation_service.infrastructure.repository.model.LanguageModel;
import huy.project.accommodation_service.infrastructure.repository.specification.LanguageSpecification;
import huy.project.accommodation_service.kernel.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public Pair<PageInfo, List<LanguageEntity>> getLanguages(GetLanguageParams params) {
        Pageable pageable = PageUtils.getPageable(params);

        var result = languageRepository.findAll(LanguageSpecification.getLanguages(params), pageable);
        return Pair.of(PageUtils.getPageInfo(result), LanguageMapper.INSTANCE.toListEntity(result.getContent()));
    }

    @Override
    public void deleteLanguageById(Long id) {
        languageRepository.deleteById(id);
    }

    @Override
    public LanguageEntity getLanguageById(Long id) {
        return languageRepository.findById(id)
                .map(LanguageMapper.INSTANCE::toEntity).orElse(null);
    }
}
