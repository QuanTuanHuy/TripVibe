package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.CacheConstant;
import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import huy.project.authentication_service.core.port.ICachePort;
import huy.project.authentication_service.core.port.IPrivilegePort;
import huy.project.authentication_service.kernel.utils.CacheUtils;
import huy.project.authentication_service.kernel.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPrivilegeUseCase {
    private final IPrivilegePort privilegePort;
    private final ICachePort cachePort;

    public List<PrivilegeEntity> getPrivilegesByIds(List<Long> ids) {
        return privilegePort.getPrivilegesByIds(ids);
    }

    public List<PrivilegeEntity> getAllPrivileges() {
        String privilegeStr = cachePort.getFromCache(CacheUtils.CACHE_ALL_PRIVILEGES);
        if (privilegeStr != null) {
            return JsonUtils.fromJsonList(privilegeStr, PrivilegeEntity.class);
        }

        List<PrivilegeEntity> privileges = privilegePort.getAllPrivileges();

        cachePort.setToCache(CacheUtils.CACHE_ALL_PRIVILEGES, privileges, CacheConstant.DEFAULT_TTL);

        return privileges;
    }
}
