package huy.project.file_service.core.port;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface IFileStoragePort {
    String storeFile(MultipartFile file);
    String storeFile(String fileName, byte[] data);
    Path getFilePath(String fileName);
    void deleteFile(String fileName);
}
