package huy.project.file_service.infrastructure.repository.mapper;

import huy.project.file_service.core.domain.entity.FileResourceEntity;
import huy.project.file_service.infrastructure.repository.model.FileResourceModel;
import huy.project.file_service.kernel.utils.JsonUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileResourceMapper {
    JsonUtils jsonUtils;

    public FileResourceModel toModel(FileResourceEntity entity) {
        if (entity == null) {
            return null;
        }

        return FileResourceModel.builder()
                .id(entity.getId())
                .fileName(entity.getFileName())
                .originalName(entity.getOriginalName())
                .size(entity.getSize())
                .contentType(entity.getContentType())
                .url(entity.getUrl())
                .createdBy(entity.getCreatedBy())
                .isImage(entity.getIsImage())
                .width(entity.getWidth())
                .height(entity.getHeight())
                .versions(jsonUtils.toJson(entity.getVersions()))
                .category(entity.getCategory())
                .referenceId(entity.getReferenceId())
                .referenceType(entity.getReferenceType())
                .tags(entity.getTags())
                .description(entity.getDescription())
                .isPublic(entity.getIsPublic())
                .groupId(entity.getGroupId())
                .build();
    }

    public FileResourceEntity toEntity(FileResourceModel model) {
        if (model == null) {
            return null;
        }

        return FileResourceEntity.builder()
                .id(model.getId())
                .fileName(model.getFileName())
                .originalName(model.getOriginalName())
                .size(model.getSize())
                .contentType(model.getContentType())
                .url(model.getUrl())
                .createdBy(model.getCreatedBy())
                .createdAt(model.getCreatedAt() != null ?
                        model.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null)
                .isImage(model.getIsImage())
                .width(model.getWidth())
                .height(model.getHeight())
                .versions(jsonUtils.fromJson(model.getVersions(), Map.class))
                .category(model.getCategory())
                .referenceId(model.getReferenceId())
                .referenceType(model.getReferenceType())
                .tags(model.getTags())
                .description(model.getDescription())
                .isPublic(model.getIsPublic())
                .groupId(model.getGroupId())
                .build();
    }

}
