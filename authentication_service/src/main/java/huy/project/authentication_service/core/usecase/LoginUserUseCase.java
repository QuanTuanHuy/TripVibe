package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.dto.request.TokenRefreshRequest;
import huy.project.authentication_service.core.domain.dto.response.LoginResponse;
import huy.project.authentication_service.core.domain.entity.RefreshTokenEntity;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.kernel.property.JwtProperty;
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
    private final CreateRefreshTokenUseCase createRefreshToken;
    private final JwtUtils jwtUtils;
    private final JwtProperty jwtProperty;
    private final PasswordEncoder passwordEncoder;
//    private final SecurityVerificationUseCase securityVerificationUseCase;
    private final VerifyRefreshTokenUseCase verifyRefreshTokenUseCase;

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
            log.error("password: {}, user password: {}", password, existedUser.getPassword());
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (!existedUser.getEnabled()) {
            log.error("User is not activated");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String accessToken = jwtUtils.generateAccessToken(existedUser);
        RefreshTokenEntity refreshToken = createRefreshToken.createRefreshToken(existedUser);
        
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtProperty.getAccessTokenExpiration())
                .tokenType("Bearer")
                .build();
    }

    public LoginResponse loginWithRefreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // Verify the refresh token is valid
        RefreshTokenEntity refreshToken = verifyRefreshTokenUseCase.verifyRefreshToken(requestRefreshToken);

        // Run security verification first
//        boolean securityPassed = securityVerificationUseCase.verifyDeviceSecurity(requestRefreshToken);
//        if (!securityPassed) {
//            log.warn("Security verification failed for refresh token");
//            throw new AppException(ErrorCode.SUSPICIOUS_TOKEN_USAGE);
//        }


        UserEntity user = getUserUseCase.getUserById(refreshToken.getUserId());

        String accessToken = jwtUtils.generateAccessToken(user);

        // Return the response with the new access token and same refresh token
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtProperty.getAccessTokenExpiration())
                .tokenType("Bearer")
                .build();
    }
}
