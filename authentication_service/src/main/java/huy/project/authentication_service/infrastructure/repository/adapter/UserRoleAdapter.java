package huy.project.authentication_service.infrastructure.repository.adapter;

import huy.project.authentication_service.core.domain.entity.UserRoleEntity;
import huy.project.authentication_service.core.port.IUserRolePort;
import huy.project.authentication_service.infrastructure.repository.IUserRoleRepository;
import huy.project.authentication_service.infrastructure.repository.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleAdapter implements IUserRolePort {
    private final IUserRoleRepository userRoleRepository;

    @Override
    public void saveAll(List<UserRoleEntity> userRoles) {
        UserRoleMapper.INSTANCE.toListEntity(
                userRoleRepository.saveAll(UserRoleMapper.INSTANCE.toListModel(userRoles)));
    }

    @Override
    public List<UserRoleEntity> getUserRolesByUserId(Long userId) {
        return UserRoleMapper.INSTANCE.toListEntity(userRoleRepository.findByUserId(userId));
    }

    @Override
    public void deleteByUserId(Long userId) {
        userRoleRepository.deleteByUserId(userId);
    }
}
