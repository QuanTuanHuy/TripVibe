package huy.project.authentication_service.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import huy.project.authentication_service.core.domain.constant.PrivilegeType;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoleEntity {
    private Long id;
    private String name;
    private String description;
    private List<PrivilegeEntity> privileges;

    @JsonIgnore
    public List<String> getPrivilegeNames() {
        return privileges.stream()
                .map(PrivilegeEntity::getName)
                .collect(Collectors.toList());
    }

    public boolean hasPrivilege(String privilegeName) {
        return getPrivilegeNames().contains(privilegeName);
    }

    public boolean hasPrivilege(PrivilegeType privilege) {
        return hasPrivilege(privilege.getPrivilegeName());
    }

    @JsonIgnore
    public int getPrivilegeCount() {
        return privileges != null ? privileges.size() : 0;
    }
}
