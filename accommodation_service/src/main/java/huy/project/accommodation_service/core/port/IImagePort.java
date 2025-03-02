package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.ImageEntity;

import java.util.List;

public interface IImagePort {
    List<ImageEntity> saveAll(List<ImageEntity> images);
    List<ImageEntity> getImagesByEntityIdAndType(Long entityId, String entityType);
    void deleteImagesByEntityIdAndType(Long entityId, String entityType);
}
