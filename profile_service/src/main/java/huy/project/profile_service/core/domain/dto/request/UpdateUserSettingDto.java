package huy.project.profile_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserSettingDto {
    private Long languageId;
    private Long currencyId;
}
