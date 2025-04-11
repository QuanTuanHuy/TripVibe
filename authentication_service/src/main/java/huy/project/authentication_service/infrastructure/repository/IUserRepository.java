package huy.project.authentication_service.infrastructure.repository;

import huy.project.authentication_service.infrastructure.repository.model.UserModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends IBaseRepository<UserModel> {
    Optional<UserModel> findByEmail(String email);
}
