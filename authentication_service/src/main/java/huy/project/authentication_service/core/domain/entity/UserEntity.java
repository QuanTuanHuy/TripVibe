package huy.project.authentication_service.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import huy.project.authentication_service.core.domain.constant.PrivilegeType;
import huy.project.authentication_service.core.domain.constant.RoleType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Builder.Default
    private List<RoleEntity> roles = new ArrayList<>();

    @JsonIgnore
    public List<String> getRoleNames() {
        return roles.stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public Set<String> getAllPrivileges() {
        return roles.stream()
                .flatMap(role -> role.getPrivilegeNames().stream())
                .collect(Collectors.toSet());
    }

    @JsonIgnore
    public boolean hasPrivilege(String privilegeName) {
        return getAllPrivileges().contains(privilegeName);
    }

    @JsonIgnore
    public boolean hasPrivilege(PrivilegeType privilege) {
        return hasPrivilege(privilege.getPrivilegeName());
    }

    @JsonIgnore
    public boolean hasRole(String roleName) {
        return getRoleNames().contains(roleName);
    }

    @JsonIgnore
    public boolean hasRole(RoleType roleType) {
        return hasRole(roleType.getRoleName());
    }

    @JsonIgnore
    public boolean isAdmin() {
        return hasRole(RoleType.ADMIN) || hasRole(RoleType.SUPER_ADMIN);
    }


    public void addRole(RoleEntity role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    public void removeRole(RoleEntity role) {
        roles.remove(role);
    }

    @JsonIgnore
    public boolean isActive() {
        return enabled != null && enabled;
    }
}
