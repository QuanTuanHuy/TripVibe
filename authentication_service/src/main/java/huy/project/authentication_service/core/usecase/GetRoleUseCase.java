package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.CacheConstant;
import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.core.domain.entity.RolePrivilegeEntity;
import huy.project.authentication_service.core.port.ICachePort;
import huy.project.authentication_service.core.port.IRolePort;
import huy.project.authentication_service.core.port.IRolePrivilegePort;
import huy.project.authentication_service.kernel.utils.CacheUtils;
import huy.project.authentication_service.kernel.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetRoleUseCase {
    private final IRolePort rolePort;
    private final IRolePrivilegePort rolePrivilegePort;
    private final ICachePort cachePort;
    private final GetPrivilegeUseCase getPrivilegeUseCase;

    public RoleEntity getRoleById(Long id) {
        String key = CacheUtils.buildCacheKeyGetRoleById(id);
        RoleEntity cachedRole = cachePort.getFromCache(key, RoleEntity.class);
        if (cachedRole != null) {
            return cachedRole;
        }

        RoleEntity role = rolePort.getRoleById(id);

        List<RolePrivilegeEntity> rolePrivileges = rolePrivilegePort.getRolePrivilegesByRoleId(id);
        List<Long> privilegeIds = rolePrivileges.stream().map(RolePrivilegeEntity::getPrivilegeId).toList();
        role.setPrivileges(getPrivilegeUseCase.getPrivilegesByIds(privilegeIds));

        cachePort.setToCache(key, role, CacheConstant.DEFAULT_TTL);
        return role;
    }

    public List<RoleEntity> getAllRoles() {
        // ger from cache
        String roleStr = cachePort.getFromCache(CacheUtils.CACHE_ALL_ROLES);
        if (roleStr != null) {
            return JsonUtils.fromJsonList(roleStr, RoleEntity.class);
        }

        // get from db
        List<RoleEntity> roles = rolePort.getAllRoles();

        List<Long> roleIds = roles.stream()
                .map(RoleEntity::getId).toList();
        List<RolePrivilegeEntity> rolePrivileges = rolePrivilegePort.getRolePrivilegesByRoleIds(roleIds);

        List<PrivilegeEntity> privileges = getPrivilegeUseCase.getAllPrivileges();
        var privilegeMap = privileges.stream()
                .collect(Collectors.toMap(PrivilegeEntity::getId, Function.identity()));

        roles.forEach(role -> {
            List<Long> privilegeIds = rolePrivileges.stream()
                    .filter(rolePrivilege -> rolePrivilege.getRoleId().equals(role.getId()))
                    .map(RolePrivilegeEntity::getPrivilegeId).toList();
            role.setPrivileges(privilegeIds.stream()
                    .map(privilegeMap::get).toList());
        });

        // set to cache
        cachePort.setToCache(CacheUtils.CACHE_ALL_ROLES, roles, CacheConstant.DEFAULT_TTL);

        return roles;
    }
}
