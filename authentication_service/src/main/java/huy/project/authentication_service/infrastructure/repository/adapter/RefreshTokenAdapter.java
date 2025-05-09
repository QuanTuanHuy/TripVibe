package huy.project.authentication_service.infrastructure.repository.adapter;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.entity.RefreshTokenEntity;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.port.IRefreshTokenPort;
import huy.project.authentication_service.infrastructure.repository.IRefreshTokenRepository;
import huy.project.authentication_service.infrastructure.repository.mapper.RefreshTokenMapper;
import huy.project.authentication_service.infrastructure.repository.model.RefreshTokenModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenAdapter implements IRefreshTokenPort {
    IRefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshTokenEntity save(RefreshTokenEntity refreshToken) {
        try {
            RefreshTokenModel model = RefreshTokenMapper.INSTANCE.toModel(refreshToken);
            return RefreshTokenMapper.INSTANCE.toEntity(refreshTokenRepository.save(model));
        } catch (Exception e) {
            throw new AppException(ErrorCode.SAVE_REFRESH_TOKEN_FAILED);
        }
    }

    @Override
    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token).map(RefreshTokenMapper.INSTANCE::toEntity);
    }

    @Override
    public List<RefreshTokenEntity> findAllByUserId(Long userId) {
        return RefreshTokenMapper.INSTANCE.toListEntity(refreshTokenRepository.findAllByUserId(userId));
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }
}