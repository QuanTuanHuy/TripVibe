package huy.project.authentication_service.core.port;

import huy.project.authentication_service.core.domain.entity.UserRoleEntity;

import java.util.List;

public interface IUserRolePort {
    void saveAll(List<UserRoleEntity> userRoles);
    List<UserRoleEntity> getUserRolesByUserId(Long userId);
    void deleteByUserId(Long userId);
}
