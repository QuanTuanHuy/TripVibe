package huy.project.profile_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserSettingEntity {
    private Long id;
    private Long languageId;
    private Long currencyId;
}
