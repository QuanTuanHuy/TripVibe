package huy.project.authentication_service.core.domain.entity;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    private Long id;
    private String email;
    private String password;
    private Boolean enabled;
    List<RoleEntity> roles;
}
