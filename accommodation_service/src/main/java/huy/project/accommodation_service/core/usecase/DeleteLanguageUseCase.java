package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.entity.LanguageEntity;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.ILanguagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteLanguageUseCase {
    private final ILanguagePort languagePort;

    @Transactional(rollbackFor = Exception.class)
    public void deleteLanguage(Long id) {
        LanguageEntity language = languagePort.getLanguageById(id);
        if (language == null) {
            throw new AppException(ErrorCode.LANGUAGE_NOT_FOUND);
        }
        languagePort.deleteLanguageById(id);
    }
}
