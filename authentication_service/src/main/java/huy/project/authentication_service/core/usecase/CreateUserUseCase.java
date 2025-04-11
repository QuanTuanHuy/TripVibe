package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.constant.OtpType;
import huy.project.authentication_service.core.domain.dto.OtpDto;
import huy.project.authentication_service.core.domain.dto.request.CreateNotificationDto;
import huy.project.authentication_service.core.domain.dto.request.CreateUserRequestDto;
import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.domain.entity.UserRoleEntity;
import huy.project.authentication_service.core.domain.mapper.UserMapper;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.port.ICachePort;
import huy.project.authentication_service.core.port.INotificationPort;
import huy.project.authentication_service.core.port.IUserPort;
import huy.project.authentication_service.core.port.IUserRolePort;
import huy.project.authentication_service.core.validation.RoleValidation;
import huy.project.authentication_service.core.validation.UserValidation;
import huy.project.authentication_service.kernel.utils.CacheUtils;
import huy.project.authentication_service.kernel.utils.OtpUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateUserUseCase {
    IUserPort userPort;
    IUserRolePort userRolePort;
    INotificationPort notificationPort;
    ICachePort cachePort;

    RoleValidation roleValidation;
    UserValidation userValidation;

    OtpUtils otpUtils;

    PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public UserEntity createUser(CreateUserRequestDto request) {
        // validate req
        if (userValidation.isEmailExist(request.getEmail())) {
            log.error("Email is already taken");
            throw new AppException(ErrorCode.USER_EMAIL_EXISTED);
        }

        Pair<Boolean, List<RoleEntity>> roleExisted = roleValidation.isRolesExist(request.getRoleIds());
        if (!roleExisted.getFirst()) {
            log.error("Some roles are not existed");
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }

        // create user
        UserEntity user = UserMapper.INSTANCE.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);
        user = userPort.save(user);
        user.setRoles(roleExisted.getSecond());
        user.setPassword(null);
        final Long userId = user.getId();

        List<UserRoleEntity> userRoles = roleExisted.getSecond().stream()
                .map(role -> UserRoleEntity.builder()
                        .userId(userId)
                        .roleId(role.getId())
                        .build())
                .toList();
        userRolePort.saveAll(userRoles);

        // send otp to user
        OtpDto otp = createOtpForRegister();
        cachePort.setToCache(CacheUtils.buildCacheKeyOtpRegister(user.getEmail()), otp, otp.getExpiredAt());
        var createNotificationDto = buildCreateNotification(user, otp);
        notificationPort.createNotification(createNotificationDto);

        return user;
    }

    private OtpDto createOtpForRegister() {
        String otp = otpUtils.generateRandomOtp(6);
        return OtpDto.builder()
                .otp(otp)
                .type(OtpType.REGISTER)
                .expiredAt(Instant.now().plusMillis(5).toEpochMilli())
                .build();
    }

    private CreateNotificationDto buildCreateNotification(UserEntity user, OtpDto otpDto) {
        return CreateNotificationDto.builder()
                .userId(user.getId())
                .type("EMAIL")
                .title("OTP Registration Confirmation")
                .content("your otp for registration of booking: " + otpDto.getOtp())
                .recipient(user.getEmail())
                .build();
    }

}
