package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.dto.request.UpdateUserRequestDto;
import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.domain.entity.UserRoleEntity;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.port.ICachePort;
import huy.project.authentication_service.core.port.IUserPort;
import huy.project.authentication_service.core.port.IUserRolePort;
import huy.project.authentication_service.core.validation.RoleValidation;
import huy.project.authentication_service.core.validation.UserValidation;
import huy.project.authentication_service.infrastructure.repository.mapper.UserMapper;
import huy.project.authentication_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUserUseCase {
    private final IUserPort userPort;
    private final IUserRolePort userRolePort;

    private final GetUserUseCase getUserUseCase;
    private final ICachePort cachePort;

    private final UserValidation userValidation;
    private final RoleValidation roleValidation;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity updateUser(Long userId, UpdateUserRequestDto req) {
        UserEntity existedUser = getUserUseCase.getUserById(userId);

        // validate req
        Pair<Boolean, ErrorCode> validateResult = userValidation.validateUpdateUserReq(existedUser, req);
        if (!validateResult.getFirst()) {
            throw new AppException(validateResult.getSecond());
        }

        Pair<Boolean, List<RoleEntity>> roleExisted = roleValidation.isRolesExist(req.getRoleIds());
        if (!roleExisted.getFirst()) {
            log.error("Some roles are not existed");
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }

        // delete user roles
        userRolePort.deleteByUserId(userId);

        // update user
        UserEntity updateUser = UserMapper.INSTANCE.toEntity(existedUser, req);
        updateUser.setPassword(passwordEncoder.encode(req.getPassword()));
        updateUser = userPort.save(updateUser);
        updateUser.setPassword(null);
        updateUser.setRoles(roleExisted.getSecond());

        // update user roles
        List<UserRoleEntity> userRoles = roleExisted.getSecond().stream()
                .map(role -> UserRoleEntity.builder()
                        .userId(userId)
                        .roleId(role.getId())
                        .build())
                .toList();
        userRolePort.saveAll(userRoles);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetUserById(userId));
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetUserByUsername(updateUser.getUsername()));

        return updateUser;
    }
}
