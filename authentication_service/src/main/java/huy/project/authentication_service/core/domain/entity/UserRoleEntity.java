package huy.project.authentication_service.core.domain.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserRoleEntity {
    private Long id;
    private Long userId;
    private Long roleId;
}
