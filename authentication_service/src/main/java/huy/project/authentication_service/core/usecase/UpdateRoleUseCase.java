package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.dto.request.UpdateRoleRequestDto;
import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.core.domain.entity.RolePrivilegeEntity;
import huy.project.authentication_service.core.domain.mapper.RoleMapper;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.port.IRolePort;
import huy.project.authentication_service.core.port.IRolePrivilegePort;
import huy.project.authentication_service.core.validation.PrivilegeValidation;
import huy.project.authentication_service.core.validation.RoleValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateRoleUseCase {
    private final IRolePort rolePort;
    private final IRolePrivilegePort rolePrivilegePort;
    private final RoleValidation roleValidation;
    private final PrivilegeValidation privilegeValidation;

    @Transactional(rollbackFor = Exception.class)
    public RoleEntity updateRole(Long roleId, UpdateRoleRequestDto req) {
        // validate req
        Pair<Boolean, RoleEntity> isRoleExist = roleValidation.isRoleExist(roleId);
        if (!isRoleExist.getFirst()) {
            log.error("Role not found");
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
        RoleEntity role = isRoleExist.getSecond();

        if (!req.getName().equals(role.getName()) && roleValidation.isRoleNameExist(req.getName())) {
            log.error("Role name existed");
            throw new AppException(ErrorCode.ROLE_NAME_EXISTED);
        }

        Pair<Boolean, List<PrivilegeEntity>> isPrivilegeExist = privilegeValidation
                .isPrivilegeExist(req.getPrivilegeIds());
        if (!isPrivilegeExist.getFirst()) {
            log.error("Privilege not found");
            throw new AppException(ErrorCode.PRIVILEGE_NOT_FOUND);
        }

        // update role
        role = RoleMapper.INSTANCE.toEntity(role, req);
        role = rolePort.save(role);
        List<PrivilegeEntity> privileges = isPrivilegeExist.getSecond();
        role.setPrivileges(privileges);

        // delete all role privileges
        rolePrivilegePort.deleteRolePrivilegesByRoleId(roleId);

        // add new role privileges
        List<RolePrivilegeEntity> rolePrivileges = privileges.stream()
                .map(privilege -> RolePrivilegeEntity.builder()
                        .roleId(roleId)
                        .privilegeId(privilege.getId())
                        .build())
                .toList();
        rolePrivilegePort.saveAll(rolePrivileges);

        return role;
    }
}
