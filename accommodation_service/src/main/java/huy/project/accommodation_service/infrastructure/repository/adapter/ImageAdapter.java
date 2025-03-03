package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.ImageEntity;
import huy.project.accommodation_service.core.port.IImagePort;
import huy.project.accommodation_service.infrastructure.repository.IImageRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.ImageMapper;
import huy.project.accommodation_service.infrastructure.repository.model.ImageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageAdapter implements IImagePort {
    private final IImageRepository imageRepository;

    @Override
    public List<ImageEntity> saveAll(List<ImageEntity> images) {
        List<ImageModel> imageModels = ImageMapper.INSTANCE.toListModel(images);
        return ImageMapper.INSTANCE.toListEntity(imageRepository.saveAll(imageModels));
    }

    @Override
    public List<ImageEntity> getImagesByEntityIdAndType(Long entityId, String entityType) {
        return ImageMapper.INSTANCE.toListEntity(imageRepository.findByEntityIdAndEntityType(entityId, entityType));
    }

    @Override
    public void deleteImagesByEntityIdAndType(Long entityId, String entityType) {
        imageRepository.deleteByEntityIdAndEntityType(entityId, entityType);
    }
}
