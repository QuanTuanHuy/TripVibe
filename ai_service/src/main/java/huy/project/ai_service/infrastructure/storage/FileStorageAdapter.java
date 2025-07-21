package huy.project.ai_service.infrastructure.storage;

import huy.project.ai_service.core.domain.constant.ErrorCode;
import huy.project.ai_service.core.exception.AppException;
import huy.project.ai_service.core.port.IFileStoragePort;
import huy.project.ai_service.kernel.properties.FileStorageProperty;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileStorageAdapter implements IFileStoragePort {
    private final FileStorageProperty fileStorageProperty;

    private Path fileStoragePath;

    @PostConstruct
    public void init() {
        try {
            fileStoragePath = Path.of(fileStorageProperty.getUploadDir());
            Files.createDirectories(fileStoragePath);
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        validateFile(file);
        String fileName = generateFileName(file);
        try {
            Path targetLocation = fileStoragePath.resolve(fileName).normalize();
            Files.write(targetLocation, file.getBytes());
            return fileName;
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path fileToDelete = fileStoragePath.resolve(filePath).normalize();
            Files.deleteIfExists(fileToDelete);
        } catch (Exception e) {
            throw new AppException(ErrorCode.DELETE_FILE_ERROR);
        }
    }

    @Override
    public byte[] loadFile(String filePath) {
        try {
            Path fileToLoad = fileStoragePath.resolve(filePath).normalize();
            if (!Files.exists(fileToLoad)) {
                throw new AppException(ErrorCode.FILE_NOT_FOUND);
            }
            return Files.readAllBytes(fileToLoad);
        } catch (Exception e) {
            throw new AppException(ErrorCode.READ_FILE_ERROR);
        }
    }

    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new AppException(ErrorCode.FILE_NAME_EMPTY);
        }
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        return System.currentTimeMillis() + "-" + java.util.UUID.randomUUID() + fileExtension;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new AppException(ErrorCode.FILE_NAME_EMPTY);
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        log.info("extension: {}", extension);
        log.info("allowed extensions: {}", fileStorageProperty.getAllowedExtensions());

        if (!fileStorageProperty.getAllowedExtensions().contains(extension)) {
            throw new AppException(ErrorCode.FILE_TYPE_NOT_SUPPORTED);
        }

        if (file.getSize() > fileStorageProperty.getMaxFileSize()) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }
    }
}
