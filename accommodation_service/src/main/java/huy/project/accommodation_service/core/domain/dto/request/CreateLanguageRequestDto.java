package huy.project.accommodation_service.core.domain.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLanguageRequestDto {
    private String name;
    private String code;
    private String nativeName;
}
