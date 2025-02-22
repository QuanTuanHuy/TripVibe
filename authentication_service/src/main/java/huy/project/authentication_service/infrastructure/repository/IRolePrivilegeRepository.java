package huy.project.authentication_service.infrastructure.repository;

import huy.project.authentication_service.infrastructure.repository.model.RolePrivilegeModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRolePrivilegeRepository extends IBaseRepository<RolePrivilegeModel> {
    List<RolePrivilegeModel> findByRoleIdIn(List<Long> roleIds);
    List<RolePrivilegeModel> findByRoleId(Long roleId);
    void deleteByRoleId(Long roleId);
}
