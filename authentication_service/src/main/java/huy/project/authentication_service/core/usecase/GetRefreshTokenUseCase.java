package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.entity.RefreshTokenEntity;
import huy.project.authentication_service.core.port.IRefreshTokenPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetRefreshTokenUseCase {
    IRefreshTokenPort refreshTokenPort;

    public List<RefreshTokenEntity> getUserSessions(Long userId) {
        return refreshTokenPort.findAllByUserId(userId);
    }
}
