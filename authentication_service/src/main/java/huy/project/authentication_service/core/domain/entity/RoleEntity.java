package huy.project.authentication_service.core.domain.entity;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RoleEntity {
    private Long id;
    private String name;
    private String description;
    private List<PrivilegeEntity> privileges;
}
