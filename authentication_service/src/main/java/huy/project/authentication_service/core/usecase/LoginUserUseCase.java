package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.dto.response.LoginResponse;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.kernel.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUserUseCase {
    private final GetUserUseCase getUserUseCase;
    private final JwtUtils jwtUtils;

    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(String email, String password) {
        UserEntity existedUser;
        try {
            existedUser = getUserUseCase.getUserByEmail(email);
        } catch (Exception e) {
            log.error("User not found");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!passwordEncoder.matches(password, existedUser.getPassword())) {
            log.error("Password is incorrect");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!existedUser.getEnabled()) {
            log.error("User is not activated");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return new LoginResponse(jwtUtils.generateToken(existedUser));
    }
}
