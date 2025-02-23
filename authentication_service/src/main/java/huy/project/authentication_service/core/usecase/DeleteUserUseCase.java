package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.port.ICachePort;
import huy.project.authentication_service.core.port.IUserPort;
import huy.project.authentication_service.core.port.IUserRolePort;
import huy.project.authentication_service.kernel.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteUserUseCase {
    private final IUserPort userPort;
    private final IUserRolePort userRolePort;
    private final ICachePort cachePort;

    private final GetUserUseCase getUserUseCase;

    @Transactional
    public void deleteUser(Long userId) {
        // check user existed
        getUserUseCase.getUserById(userId);

        userRolePort.deleteByUserId(userId);
        userPort.deleteUserById(userId);

        // clear cache
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyGetUserById(userId));
    }
}
