package huy.project.accommodation_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LanguageEntity {
    private Long id;
    private String name;
    private String code;
    private String nativeName;
}
