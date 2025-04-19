package huy.project.accommodation_service.infrastructure.repository.mapper;

import huy.project.accommodation_service.core.domain.entity.ImageEntity;
import huy.project.accommodation_service.infrastructure.repository.model.ImageModel;
import huy.project.accommodation_service.kernel.utils.JsonUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ImageMapper {
    JsonUtils jsonUtils;

    public ImageModel toModel(ImageEntity entity) {
        if (entity == null) {
            return null;
        }

        return ImageModel.builder()
                .id(entity.getId())
                .url(entity.getUrl())
                .entityId(entity.getEntityId())
                .entityType(entity.getEntityType())
                .isPrimary(entity.getIsPrimary())
                .versions(jsonUtils.toJson(entity.getVersions()))
                .build();
    }

    public ImageEntity toEntity(ImageModel model) {
        if (model == null) {
            return null;
        }

        return ImageEntity.builder()
                .id(model.getId())
                .url(model.getUrl())
                .entityId(model.getEntityId())
                .entityType(model.getEntityType())
                .isPrimary(model.getIsPrimary())
                .versions(jsonUtils.fromJson(model.getVersions(), Map.class))
                .build();
    }

    public List<ImageModel> toListModel(List<ImageEntity> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toModel)
                .toList();
    }

    public List<ImageEntity> toListEntity(List<ImageModel> models) {
        if (models == null) {
            return null;
        }

        return models.stream()
                .map(this::toEntity)
                .toList();
    }
}
