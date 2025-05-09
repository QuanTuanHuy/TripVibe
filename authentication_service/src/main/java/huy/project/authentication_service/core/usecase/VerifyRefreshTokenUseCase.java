package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.entity.RefreshTokenEntity;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.port.IRefreshTokenPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VerifyRefreshTokenUseCase {
    IRefreshTokenPort refreshTokenPort;

    public RefreshTokenEntity verifyRefreshToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenPort.findByToken(token).orElseThrow(
                () -> {
                    log.error("Refresh token not found");
                    return new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
                });

        if (refreshToken.isExpired()) {
            log.error("Refresh token is expired");
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        return refreshToken;
    }
}
