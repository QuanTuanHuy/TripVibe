package huy.project.authentication_service.infrastructure.repository;

import huy.project.authentication_service.infrastructure.repository.model.PrivilegeModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPrivilegeRepository extends IBaseRepository<PrivilegeModel> {
    Optional<PrivilegeModel> findByName(String name);
    List<PrivilegeModel> findByIdIn(List<Long> ids);
}
