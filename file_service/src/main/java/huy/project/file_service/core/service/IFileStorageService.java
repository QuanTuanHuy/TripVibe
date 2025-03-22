package huy.project.file_service.core.service;

import huy.project.file_service.core.domain.entity.FileResourceEntity;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {
    FileResourceEntity storeFile(Long userId, MultipartFile file);
    Pair<Resource, FileResourceEntity> downloadFile(Long id);
}
