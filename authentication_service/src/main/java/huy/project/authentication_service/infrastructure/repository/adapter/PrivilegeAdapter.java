package huy.project.authentication_service.infrastructure.repository.adapter;

import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import huy.project.authentication_service.core.port.IPrivilegePort;
import huy.project.authentication_service.infrastructure.repository.IPrivilegeRepository;
import huy.project.authentication_service.infrastructure.repository.mapper.PrivilegeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivilegeAdapter implements IPrivilegePort {
    private final IPrivilegeRepository privilegeRepository;

    @Override
    public PrivilegeEntity save(PrivilegeEntity privilege) {
        return PrivilegeMapper.INSTANCE.toEntity(privilegeRepository.save(PrivilegeMapper.INSTANCE.toModel(privilege)));
    }

    @Override
    public PrivilegeEntity getPrivilegeByName(String name) {
        return PrivilegeMapper.INSTANCE.toEntity(privilegeRepository.findByName(name).orElse(null));
    }

    @Override
    public PrivilegeEntity getPrivilegeById(Long id) {
        return PrivilegeMapper.INSTANCE.toEntity(privilegeRepository.findById(id).orElse(null));
    }

    @Override
    public List<PrivilegeEntity> getAllPrivileges() {
        return PrivilegeMapper.INSTANCE.toListPrivilegeEntity(privilegeRepository.findAll());
    }

    @Override
    public List<PrivilegeEntity> getPrivilegesByIds(List<Long> privilegeIds) {
        return PrivilegeMapper.INSTANCE.toListPrivilegeEntity(privilegeRepository.findByIdIn(privilegeIds));
    }
}
