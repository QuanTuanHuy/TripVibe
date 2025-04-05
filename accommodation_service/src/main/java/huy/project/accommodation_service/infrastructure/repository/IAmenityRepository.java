package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.AmenityModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAmenityRepository extends IBaseRepository<AmenityModel> {
    Optional<AmenityModel> findByName(String name);
    List<AmenityModel> findByGroupId(Long groupId);
    List<AmenityModel> findByGroupIdIn(List<Long> groupIds);
    void deleteByGroupId(Long groupId);
    List<AmenityModel> findByIdIn(List<Long> ids);
}
