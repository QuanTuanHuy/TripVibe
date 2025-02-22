package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import huy.project.authentication_service.core.port.IPrivilegePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPrivilegeUseCase {
    private final IPrivilegePort privilegePort;

    public List<PrivilegeEntity> getPrivilegesByIds(List<Long> ids) {
        return privilegePort.getPrivilegesByIds(ids);
    }

    public List<PrivilegeEntity> getAllPrivileges() {
        return privilegePort.getAllPrivileges();
    }
}
