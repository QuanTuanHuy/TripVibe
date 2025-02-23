package huy.project.authentication_service.infrastructure.repository;

import huy.project.authentication_service.infrastructure.repository.model.RoleModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRoleRepository extends IBaseRepository<RoleModel> {
    Optional<RoleModel> findByName(String name);
    List<RoleModel> findByIdIn(List<Long> ids);
}
