package huy.project.accommodation_service.core.validation;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.request.CreateLanguageRequestDto;
import huy.project.accommodation_service.core.port.ILanguagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LanguageValidation {
    private final ILanguagePort languagePort;

    private boolean languageNameExist(String name) {
        return languagePort.getLanguageByName(name) != null;
    }

    private boolean languageCodeExist(String code) {
        return languagePort.getLanguageByCode(code) != null;
    }

    public Pair<Boolean, ErrorCode> validateCreateLanguageRequest(CreateLanguageRequestDto req) {
        if (languageNameExist(req.getName())) {
            return Pair.of(false, ErrorCode.LANGUAGE_NAME_EXISTED);
        }

        if (languageCodeExist(req.getCode())) {
            return Pair.of(false, ErrorCode.LANGUAGE_CODE_EXISTED);
        }

        return Pair.of(true, ErrorCode.SUCCESS);
    }
}
