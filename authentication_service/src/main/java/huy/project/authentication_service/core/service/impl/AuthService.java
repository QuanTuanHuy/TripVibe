package huy.project.authentication_service.core.service.impl;

import huy.project.authentication_service.core.domain.dto.request.TokenRefreshRequest;
import huy.project.authentication_service.core.domain.dto.response.LoginResponse;
import huy.project.authentication_service.core.domain.entity.RefreshTokenEntity;
import huy.project.authentication_service.core.service.IAuthService;
import huy.project.authentication_service.core.usecase.GetRefreshTokenUseCase;
import huy.project.authentication_service.core.usecase.LoginUserUseCase;
import huy.project.authentication_service.core.usecase.LogoutUseCase;
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
public class AuthService implements IAuthService {
    LoginUserUseCase loginUserUseCase;
    LogoutUseCase logoutUseCase;
    GetRefreshTokenUseCase getRefreshTokenUseCase;

    @Override
    public LoginResponse login(String email, String password) {
        return loginUserUseCase.login(email, password);
    }

    @Override
    public LoginResponse loginWithRefreshToken(TokenRefreshRequest request) {
        return loginUserUseCase.loginWithRefreshToken(request);
    }

    @Override
    public void logout(Long userId, String refreshToken) {
        logoutUseCase.logout(userId, refreshToken);
    }
    
    @Override
    public void logoutAll(Long userId) {
        logoutUseCase.logoutAll(userId);
    }
    
    @Override
    public List<RefreshTokenEntity> getUserSessions(Long userId) {
        return getRefreshTokenUseCase.getUserSessions(userId);
    }
}
