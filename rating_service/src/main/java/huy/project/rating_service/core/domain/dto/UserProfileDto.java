package huy.project.rating_service.core.domain.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserProfileDto {
    private Long userId;
    private String userName;
}
