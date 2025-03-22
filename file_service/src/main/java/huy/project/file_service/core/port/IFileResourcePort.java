package huy.project.file_service.core.port;

import huy.project.file_service.core.domain.entity.FileResourceEntity;

public interface IFileResourcePort {
    FileResourceEntity save(FileResourceEntity fileResource);
    FileResourceEntity getFileResourceById(Long id);
}
