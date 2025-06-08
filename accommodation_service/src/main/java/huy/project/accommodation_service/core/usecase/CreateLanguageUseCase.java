package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.dto.request.CreateLanguageRequestDto;
import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import huy.project.accommodation_service.core.domain.mapper.LanguageMapper;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.ILanguagePort;
import huy.project.accommodation_service.core.validation.LanguageValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateLanguageUseCase {
    private final ILanguagePort languagePort;

    private final LanguageValidation languageValidation;

    @Transactional(rollbackFor = Exception.class)
    public LanguageEntity createLanguage(CreateLanguageRequestDto req) {
        var validationResult = languageValidation.validateCreateLanguageRequest(req);
        if (!validationResult.getFirst()) {
            log.error("create new language failed, err: {}", validationResult.getSecond().getMessage());
            throw new AppException(validationResult.getSecond());
        }

        LanguageEntity language = LanguageMapper.INSTANCE.toEntity(req);
        return languagePort.save(language);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createIfNotExists(List<CreateLanguageRequestDto> languages) {
        if (languagePort.countAll() > 0) {
            log.info("Languages already exist, skipping creation.");
            return;
        }

        languages.forEach(language -> {
            try {
                createLanguage(language);
            } catch (Exception e) {
                log.warn("Failed to create language: {}, error: {}", language.getName(), e.getMessage());
            }
        });
    }
}
