package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.ImageEntity;
import huy.project.accommodation_service.core.port.IImagePort;
import huy.project.accommodation_service.infrastructure.repository.IImageRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.ImageMapper;
import huy.project.accommodation_service.infrastructure.repository.model.ImageModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ImageAdapter implements IImagePort {
    IImageRepository imageRepository;
    ImageMapper imageMapper;

    @Override
    public List<ImageEntity> saveAll(List<ImageEntity> images) {
        List<ImageModel> imageModels = imageMapper.toListModel(images);
        return imageMapper.toListEntity(imageRepository.saveAll(imageModels));
    }

    @Override
    public List<ImageEntity> getImagesByEntityIdAndType(Long entityId, String entityType) {
        return imageMapper.toListEntity(imageRepository.findByEntityIdAndEntityType(entityId, entityType));
    }

    @Override
    public List<ImageEntity> getImagesByEntityIdsAndType(List<Long> entityIds, String entityType) {
        return imageMapper.toListEntity(imageRepository.findByEntityIdInAndEntityType(entityIds, entityType));
    }

    @Override
    public void deleteImagesByEntityIdAndType(Long entityId, String entityType) {
        imageRepository.deleteByEntityIdAndEntityType(entityId, entityType);
    }

    @Override
    public void deleteImagesByIds(List<Long> ids) {
        imageRepository.deleteByIdIn(ids);
    }
}
