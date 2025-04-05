package huy.project.profile_service.core.domain.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserProfileDto {
    private Long userId;
    private String name;
    private String email;
    private Long countryId;
    private String countryName;
}

