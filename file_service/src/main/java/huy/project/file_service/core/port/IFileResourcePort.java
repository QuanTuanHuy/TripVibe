package huy.project.file_service.core.port;

import huy.project.file_service.core.domain.entity.FileResourceEntity;

import java.util.List;

public interface IFileResourcePort {
    List<FileResourceEntity> saveAll(List<FileResourceEntity> fileResources);
    FileResourceEntity getFileResourceById(Long id);
    FileResourceEntity getFileResourceByName(String fileName);
    void deleteFileById(Long id);
}
