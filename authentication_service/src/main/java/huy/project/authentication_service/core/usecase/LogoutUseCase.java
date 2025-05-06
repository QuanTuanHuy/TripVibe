package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.port.IRefreshTokenPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LogoutUseCase {
    IRefreshTokenPort refreshTokenPort;
    VerifyRefreshTokenUseCase verifyRefreshTokenUseCase;

    @Transactional(rollbackFor = Exception.class)
    public void logout(Long userId, String refreshToken) {
        var refreshTokenEntity = verifyRefreshTokenUseCase.verifyRefreshToken(refreshToken);
        if (!refreshTokenEntity.getUserId().equals(userId)) {
            log.error("User id {} does not match with refresh token", userId);
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        refreshTokenPort.deleteByToken(refreshToken);
    }

    @Transactional(rollbackFor = Exception.class)
    public void logoutAll(Long userId) {
        refreshTokenPort.deleteAllByUserId(userId);
    }
}
