package huy.project.authentication_service.infrastructure.repository;

import huy.project.authentication_service.infrastructure.repository.model.UserRoleModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserRoleRepository extends IBaseRepository<UserRoleModel> {
    List<UserRoleModel> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
