package huy.project.authentication_service.core.port;

import huy.project.authentication_service.core.domain.entity.RolePrivilegeEntity;

import java.util.List;

public interface IRolePrivilegePort {
    void saveAll(List<RolePrivilegeEntity> rolePrivileges);
    List<RolePrivilegeEntity> getRolePrivilegesByRoleIds(List<Long> roleIds);
    List<RolePrivilegeEntity> getRolePrivilegesByRoleId(Long roleId);
    void deleteRolePrivilegesByRoleId(Long roleId);
}
