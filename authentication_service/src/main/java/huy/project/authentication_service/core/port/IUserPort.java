package huy.project.authentication_service.core.port;

import huy.project.authentication_service.core.domain.entity.UserEntity;

public interface IUserPort {
    UserEntity save(UserEntity user);
    UserEntity getUserByEmail(String email);
    UserEntity getUserById(Long id);
    void deleteUserById(Long id);
}
