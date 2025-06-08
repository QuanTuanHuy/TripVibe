package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.CacheConstant;
import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.domain.entity.UserRoleEntity;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.port.ICachePort;
import huy.project.authentication_service.core.port.IUserPort;
import huy.project.authentication_service.core.port.IUserRolePort;
import huy.project.authentication_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserUseCase {
    private final IUserPort userPort;
    private final IUserRolePort userRolePort;
    private final GetRoleUseCase getRoleUseCase;

    private final ICachePort cachePort;

    public UserEntity getUserById(Long userId) {
        // get from cache
        String key = CacheUtils.buildCacheKeyGetUserById(userId);
        UserEntity cachedUser = cachePort.getFromCache(key, UserEntity.class);
        if (cachedUser != null) {
            return cachedUser;
        }

        // get from db
        UserEntity user = userPort.getUserById(userId);
        if (user == null) {
            log.error("User not found");
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        setRolesToUser(user);

        // set to cache
        cachePort.setToCache(key, user, CacheConstant.DEFAULT_TTL);

        return user;
    }

    public UserEntity getUserByEmail(String email) {
        // get from cache
        String key = CacheUtils.buildCacheKeyGetUserByEmail(email);
        UserEntity cachedUser = cachePort.getFromCache(key, UserEntity.class);
        if (cachedUser != null) {
            return cachedUser;
        }

        // get from db
        UserEntity user = userPort.getUserByEmail(email);
        if (user == null) {
            log.error("user with email {} not found", email);
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        setRolesToUser(user);

        // set to cache
        cachePort.setToCache(key, user, CacheConstant.DEFAULT_TTL);

        return user;
    }

    private void setRolesToUser(UserEntity user) {
        List<UserRoleEntity> userRoles = userRolePort.getUserRolesByUserId(user.getId());
        List<Long> roleIds = userRoles.stream().map(UserRoleEntity::getRoleId).toList();
        List<RoleEntity> roles = getRoleUseCase.getAllRoles().stream()
                .filter(role -> roleIds.contains(role.getId()))
                .collect(Collectors.toList());
        user.setRoles(roles);
    }

    public long countAll() {
        return userPort.countAll();
    }
}
