package huy.project.authentication_service.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    private Long id;
    private String name;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private Boolean enabled;
    List<RoleEntity> roles;
}
