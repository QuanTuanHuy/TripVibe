package huy.project.file_service.core.service;

import huy.project.file_service.core.domain.dto.request.StoreFileClassificationDto;
import huy.project.file_service.core.domain.entity.FileResourceEntity;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileStorageService {
    List<FileResourceEntity> storeFiles(Long userId, MultipartFile[] files);
    Pair<Resource, FileResourceEntity> downloadFile(Long id);
    Pair<Resource, FileResourceEntity> downloadFile(String fileName);
    void deleteFiles(Long userId, List<Long> id);
    List<FileResourceEntity> storeFilesWithClassification(
            Long userId, MultipartFile[] files, StoreFileClassificationDto storeFileClassificationDto);
}
