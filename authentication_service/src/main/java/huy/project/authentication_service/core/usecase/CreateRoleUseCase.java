package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.dto.request.CreateRoleRequestDto;
import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.core.domain.entity.RolePrivilegeEntity;
import huy.project.authentication_service.core.domain.mapper.RoleMapper;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.port.ICachePort;
import huy.project.authentication_service.core.port.IRolePort;
import huy.project.authentication_service.core.port.IRolePrivilegePort;
import huy.project.authentication_service.core.validation.PrivilegeValidation;
import huy.project.authentication_service.core.validation.RoleValidation;
import huy.project.authentication_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateRoleUseCase {
    private final IRolePort rolePort;
    private final IRolePrivilegePort rolePrivilegePort;
    private final ICachePort cachePort;
    private final RoleValidation roleValidation;
    private final PrivilegeValidation privilegeValidation;

    @Transactional(rollbackFor = Exception.class)
    public RoleEntity createRole(CreateRoleRequestDto req) {
        if (roleValidation.isRoleNameExist(req.getName())) {
            log.error("Role name existed");
            throw new AppException(ErrorCode.ROLE_NAME_EXISTED);
        }

        Pair<Boolean, List<PrivilegeEntity>> isPrivilegeExist = privilegeValidation.isPrivilegeExist(req.getPrivilegeIds());
        if (!isPrivilegeExist.getFirst()) {
            log.error("Privilege not found");
            throw new AppException(ErrorCode.PRIVILEGE_NOT_FOUND);
        }
        List<PrivilegeEntity> privileges = isPrivilegeExist.getSecond();

        RoleEntity role = RoleMapper.INSTANCE.toEntity(req);
        role = rolePort.save(role);
        role.setPrivileges(privileges);
        final Long roleId = role.getId();

        List<RolePrivilegeEntity> rolePrivileges = privileges.stream()
                .map(privilege -> RolePrivilegeEntity.builder()
                        .roleId(roleId)
                        .privilegeId(privilege.getId())
                        .build())
                .toList();
        rolePrivilegePort.saveAll(rolePrivileges);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.CACHE_ALL_ROLES);

        return role;
    }

}
