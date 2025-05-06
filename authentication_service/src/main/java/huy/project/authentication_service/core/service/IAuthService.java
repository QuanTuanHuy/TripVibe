package huy.project.authentication_service.core.service;

import huy.project.authentication_service.core.domain.dto.request.TokenRefreshRequest;
import huy.project.authentication_service.core.domain.dto.response.LoginResponse;
import huy.project.authentication_service.core.domain.entity.RefreshTokenEntity;

import java.util.List;

public interface IAuthService {
    LoginResponse login(String email, String password);
    LoginResponse loginWithRefreshToken(TokenRefreshRequest request);
    void logout(Long userId, String refreshToken);
    void logoutAll(Long userId);
    List<RefreshTokenEntity> getUserSessions(Long userId);
}
