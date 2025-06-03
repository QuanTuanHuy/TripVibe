package huy.project.authentication_service.core.service.impl;

import huy.project.authentication_service.core.domain.dto.OtpDto;
import huy.project.authentication_service.core.domain.dto.request.UpdateUserRequestDto;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.port.ICachePort;
import huy.project.authentication_service.core.service.IUserService;
import huy.project.authentication_service.core.usecase.*;
import huy.project.authentication_service.kernel.utils.CacheUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService implements IUserService {
    CreateUserUseCase createUserUseCase;
    GetUserUseCase getUserUseCase;
    UpdateUserUseCase updateUserUseCase;
    DeleteUserUseCase deleteUserUseCase;
    VerifyOtpUseCase verifyOtpUseCase;

    private final ICachePort cachePort;

    @Override
    public void verifyOtpForRegister(String email, String otp) {
        verifyOtpUseCase.verifyOtpForRegister(email, otp);
    }

    @Override
    public UserEntity getDetailUser(Long id) {
        return getUserUseCase.getUserById(id);
    }

    @Override
    public UserEntity updateUser(Long userId, UpdateUserRequestDto req) {
        return updateUserUseCase.updateUser(userId, req);
    }

    @Override
    public void deleteUser(Long userId) {
        deleteUserUseCase.deleteUser(userId);
    }

    @Override
    public void createIfNotExists(String email, String password, List<String> roleNames) {
        try {
            UserEntity user = createUser(email, password, roleNames);
            OtpDto cachedOtp = cachePort.getFromCache(CacheUtils.buildCacheKeyOtpRegister(user.getEmail()), OtpDto.class);
            if (cachedOtp == null) {
                log.warn("OTP verification failed for {}", email);
                return;
            }
            verifyOtpForRegister(user.getEmail(), cachedOtp.getOtp());
            log.info("User with email {} created successfully.", email);
        } catch (Exception e) {
            log.warn("User with email {} already exists, skipping creation.", email);
        }
    }

    @Override
    public UserEntity createUser(String email, String password, List<String> roleNames) {
        return createUserUseCase.createUser(email, password, roleNames);
    }

    @Override
    public long countAll() {
        return getUserUseCase.countAll();
    }

}
