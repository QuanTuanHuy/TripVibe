package huy.project.authentication_service.core.validation;

import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.core.port.IRolePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RoleValidation {
    private final IRolePort rolePort;

    public boolean isRoleNameExist(String roleName) {
        return rolePort.getRoleByName(roleName) != null;
    }

    public Pair<Boolean, RoleEntity> isRoleExist(Long roleId) {
        RoleEntity role = rolePort.getRoleById(roleId);
        return Pair.of(role != null, role);
    }

    public Pair<Boolean, List<RoleEntity>> isRolesExist(List<Long> roleIds) {
        List<RoleEntity> roles = rolePort.getRolesByIds(roleIds);
        return Pair.of(roles.size() == roleIds.size(), roles);
    }
}
