package huy.project.authentication_service.core.validation;

import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import huy.project.authentication_service.core.port.IPrivilegePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivilegeValidation {
    private final IPrivilegePort privilegePort;

    public Pair<Boolean, List<PrivilegeEntity>> isPrivilegeExist(List<Long> privilegeIds) {
        List<PrivilegeEntity> existPrivileges = privilegePort.getPrivilegesByIds(privilegeIds);
        boolean result = existPrivileges.size() == privilegeIds.size();
        return Pair.of(result, existPrivileges);
    }

    public Pair<Boolean, PrivilegeEntity> isPrivilegeExist(Long privilegeId) {
        PrivilegeEntity privilege = privilegePort.getPrivilegeById(privilegeId);
        return Pair.of(privilege != null, privilege);
    }

    public boolean isPrivilegeNameExist(String privilegeName) {
        return privilegePort.getPrivilegeByName(privilegeName) != null;
    }
}
