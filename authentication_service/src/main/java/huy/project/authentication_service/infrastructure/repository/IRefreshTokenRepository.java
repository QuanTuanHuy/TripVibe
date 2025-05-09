package huy.project.authentication_service.infrastructure.repository;

import huy.project.authentication_service.infrastructure.repository.model.RefreshTokenModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRefreshTokenRepository extends IBaseRepository<RefreshTokenModel> {
    Optional<RefreshTokenModel> findByToken(String token);
    List<RefreshTokenModel> findAllByUserId(Long userId);
    void deleteByToken(String token);
    void deleteAllByUserId(Long userId);
}