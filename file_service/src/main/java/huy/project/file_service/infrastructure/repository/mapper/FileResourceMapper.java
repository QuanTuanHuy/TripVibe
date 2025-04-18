package huy.project.file_service.infrastructure.repository.mapper;

import huy.project.file_service.core.domain.entity.FileResourceEntity;
import huy.project.file_service.infrastructure.repository.model.FileResourceModel;

import java.time.ZoneId;

public class FileResourceMapper {
    public static FileResourceModel toModel(FileResourceEntity entity) {
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
                .category(entity.getCategory())
                .referenceId(entity.getReferenceId())
                .referenceType(entity.getReferenceType())
                .tags(entity.getTags())
                .description(entity.getDescription())
                .isPublic(entity.getIsPublic())
                .groupId(entity.getGroupId())
                .build();
    }

    public static FileResourceEntity toEntity(FileResourceModel model) {
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
