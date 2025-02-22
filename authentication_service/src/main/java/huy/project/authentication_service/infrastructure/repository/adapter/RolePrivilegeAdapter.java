package huy.project.authentication_service.infrastructure.repository.adapter;

import huy.project.authentication_service.core.domain.entity.RolePrivilegeEntity;
import huy.project.authentication_service.core.port.IRolePrivilegePort;
import huy.project.authentication_service.infrastructure.repository.IRolePrivilegeRepository;
import huy.project.authentication_service.infrastructure.repository.mapper.RolePrivilegeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolePrivilegeAdapter implements IRolePrivilegePort {
    private final IRolePrivilegeRepository rolePrivilegeRepository;

    @Override
    public void saveAll(List<RolePrivilegeEntity> rolePrivileges) {
        RolePrivilegeMapper.INSTANCE.toListEntity(
                rolePrivilegeRepository.saveAll(RolePrivilegeMapper.INSTANCE.toListModel(rolePrivileges))
        );
    }

    @Override
    public List<RolePrivilegeEntity> getRolePrivilegesByRoleIds(List<Long> roleIds) {
        return RolePrivilegeMapper.INSTANCE.toListEntity(rolePrivilegeRepository.findByRoleIdIn(roleIds));
    }

    @Override
    public List<RolePrivilegeEntity> getRolePrivilegesByRoleId(Long roleId) {
        return RolePrivilegeMapper.INSTANCE.toListEntity(rolePrivilegeRepository.findByRoleId(roleId));
    }

    @Override
    public void deleteRolePrivilegesByRoleId(Long roleId) {
        rolePrivilegeRepository.deleteByRoleId(roleId);
    }
}
