package huy.project.authentication_service.core.service.impl;

import huy.project.authentication_service.core.domain.dto.request.CreateRoleRequestDto;
import huy.project.authentication_service.core.domain.dto.request.UpdateRoleRequestDto;
import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.core.service.IRoleService;
import huy.project.authentication_service.core.usecase.CreateRoleUseCase;
import huy.project.authentication_service.core.usecase.GetRoleUseCase;
import huy.project.authentication_service.core.usecase.UpdateRoleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final CreateRoleUseCase createRoleUseCase;
    private final GetRoleUseCase getRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;

    @Override
    public RoleEntity createRole(CreateRoleRequestDto req) {
        return createRoleUseCase.createRole(req);
    }

    @Override
    public List<RoleEntity> getAllRoles() {
        return getRoleUseCase.getAllRoles();
    }

    @Override
    public RoleEntity updateRole(Long roleId, UpdateRoleRequestDto req) {
        return updateRoleUseCase.updateRole(roleId, req);
    }

    @Override
    public void createIfNotExists(List<CreateRoleRequestDto> roles) {
        createRoleUseCase.createIfNotExists(roles);
    }
}
