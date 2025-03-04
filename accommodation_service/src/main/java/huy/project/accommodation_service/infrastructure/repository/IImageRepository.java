package huy.project.accommodation_service.infrastructure.repository;

import huy.project.accommodation_service.infrastructure.repository.model.ImageModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IImageRepository extends IBaseRepository<ImageModel> {
    List<ImageModel> findByEntityIdAndEntityType(Long entityId, String entityType);
    List<ImageModel> findByEntityIdInAndEntityType(List<Long> entityIds, String entityType);
    void deleteByEntityIdAndEntityType(Long entityId, String entityType);
}
