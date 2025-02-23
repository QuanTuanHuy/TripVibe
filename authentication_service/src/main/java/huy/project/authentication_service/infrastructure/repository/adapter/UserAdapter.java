package huy.project.authentication_service.infrastructure.repository.adapter;

import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.port.IUserPort;
import huy.project.authentication_service.infrastructure.repository.IUserRepository;
import huy.project.authentication_service.infrastructure.repository.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAdapter implements IUserPort {
    private final IUserRepository userRepository;

    @Override
    public UserEntity save(UserEntity user) {
        return UserMapper.INSTANCE.toEntity(userRepository.save(UserMapper.INSTANCE.toModel(user)));
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return UserMapper.INSTANCE.toEntity(userRepository.findByUsername(username).orElse(null));
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return UserMapper.INSTANCE.toEntity(userRepository.findByEmail(email).orElse(null));
    }

    @Override
    public UserEntity getUserById(Long id) {
        return UserMapper.INSTANCE.toEntity(userRepository.findById(id).orElse(null));
    }
}
