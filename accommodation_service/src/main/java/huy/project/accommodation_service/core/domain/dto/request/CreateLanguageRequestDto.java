package huy.project.accommodation_service.core.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLanguageRequestDto {
    private String name;
    private String code;
    private String nativeName;
}
