package huy.project.ai_service.core.port;

import org.springframework.web.multipart.MultipartFile;

public interface IFileStoragePort {
    String storeFile(MultipartFile file);

    void deleteFile(String filePath);

    byte[] loadFile(String filePath);
}