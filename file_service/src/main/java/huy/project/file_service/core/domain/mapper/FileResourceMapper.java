package huy.project.file_service.core.domain.mapper;

import huy.project.file_service.core.domain.dto.response.FileResourceResponse;
import huy.project.file_service.core.domain.entity.FileResourceEntity;

public class FileResourceMapper {
    public static FileResourceResponse toResponse(FileResourceEntity entity) {
        return FileResourceResponse.builder()
                .id(entity.getId())
                .fileName(entity.getFileName())
                .originalName(entity.getOriginalName())
                .contentType(entity.getContentType())
                .url(entity.getUrl())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .versions(entity.getVersions())
                .referenceId(entity.getReferenceId())
                .referenceType(entity.getReferenceType())
                .build();
    }
}
