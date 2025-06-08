package huy.project.authentication_service.core.port;

import huy.project.authentication_service.core.domain.entity.RoleEntity;

import java.util.List;

public interface IRolePort {
    RoleEntity save(RoleEntity role);
    RoleEntity getRoleByName(String name);
    RoleEntity getRoleById(Long id);
    List<RoleEntity> getRolesByIds(List<Long> ids);
    List<RoleEntity> getAllRoles();
    long countAll();
}
