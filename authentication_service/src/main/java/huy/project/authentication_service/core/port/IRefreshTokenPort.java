package huy.project.authentication_service.core.port;

import huy.project.authentication_service.core.domain.entity.RefreshTokenEntity;

import java.util.List;
import java.util.Optional;

public interface IRefreshTokenPort {
    RefreshTokenEntity save(RefreshTokenEntity refreshToken);
    Optional<RefreshTokenEntity> findByToken(String token);
    List<RefreshTokenEntity> findAllByUserId(Long userId);
    void deleteByToken(String token);
    void deleteAllByUserId(Long userId);
}