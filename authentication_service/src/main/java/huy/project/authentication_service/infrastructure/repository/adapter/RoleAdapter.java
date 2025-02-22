package huy.project.authentication_service.infrastructure.repository.adapter;

import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.core.port.IRolePort;
import huy.project.authentication_service.infrastructure.repository.IRoleRepository;
import huy.project.authentication_service.infrastructure.repository.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleAdapter implements IRolePort {
    private final IRoleRepository roleRepository;

    @Override
    public RoleEntity save(RoleEntity role) {
        return RoleMapper.INSTANCE.toEntity(roleRepository.save(RoleMapper.INSTANCE.toModel(role)));
    }

    @Override
    public RoleEntity getRoleByName(String name) {
        return RoleMapper.INSTANCE.toEntity(roleRepository.findByName(name).orElse(null));
    }

    @Override
    public RoleEntity getRoleById(Long id) {
        return RoleMapper.INSTANCE.toEntity(roleRepository.findById(id).orElse(null));
    }

    @Override
    public List<RoleEntity> getAllRoles() {
        return RoleMapper.INSTANCE.toListRoleEntity(roleRepository.findAll());
    }
}
