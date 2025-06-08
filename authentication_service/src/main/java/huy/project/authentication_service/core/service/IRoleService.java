package huy.project.authentication_service.core.service;

import huy.project.authentication_service.core.domain.dto.request.CreateRoleRequestDto;
import huy.project.authentication_service.core.domain.dto.request.UpdateRoleRequestDto;
import huy.project.authentication_service.core.domain.entity.RoleEntity;

import java.util.List;

public interface IRoleService {
    RoleEntity createRole(CreateRoleRequestDto req);
    List<RoleEntity> getAllRoles();
    RoleEntity updateRole(Long roleId, UpdateRoleRequestDto req);
    void createIfNotExists(List<CreateRoleRequestDto> roles);
}
