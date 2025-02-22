package huy.project.authentication_service.core.port;

import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;

import java.util.List;

public interface IPrivilegePort {
    PrivilegeEntity save(PrivilegeEntity privilege);
    PrivilegeEntity getPrivilegeByName(String name);
    PrivilegeEntity getPrivilegeById(Long id);
    List<PrivilegeEntity> getAllPrivileges();
    List<PrivilegeEntity> getPrivilegesByIds(List<Long> privilegeIds);
}
