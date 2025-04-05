package huy.project.file_service.infrastructure.storage;

import huy.project.file_service.core.domain.constant.ErrorCode;
import huy.project.file_service.core.exception.AppException;
import huy.project.file_service.core.port.IFileStoragePort;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageAdapter implements IFileStoragePort {

    @Value("${app.file-storage.upload-dir}")
    private String uploadDir;

    @Value("${app.file-storage.allowed-extensions}")
    private String[] allowedExtensions;

    @Value("${app.file-storage.max-file-size}")
    private long maxFileSize;

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        try {
            fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(fileStorageLocation);
        } catch (IOException ex) {
            log.error("Could not create upload directory", ex);
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        validateFile(file);
        String fileName = generateFileName(file);

        try {
            Path targetLocation = getFilePath(fileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            return fileName;
        } catch (IOException ex) {
            log.error("Could not store file", ex);
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }
    }

    @Override
    public Path getFilePath(String fileName) {
        return fileStorageLocation.resolve(fileName).normalize();
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            Path filePath = getFilePath(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Could not delete file, err: ", e);
            throw new AppException(ErrorCode.DELETE_FILE_ERROR);
        }
    }

    private String generateFileName(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = getFileExtension(originalFileName);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        return String.format("%s_%s.%s", timestamp, uuid, extension);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            log.error("File is empty");
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        if (file.getSize() > maxFileSize) {
            log.error("File is too large");
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

        // Check file extension
        String extension = getFileExtension(file.getOriginalFilename());
        boolean isAllowed = false;
        for (String allowedExt : allowedExtensions) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            log.error("File extension is not supported");
            throw new AppException(ErrorCode.FILE_TYPE_NOT_SUPPORTED);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0) return "";
        return fileName.substring(lastDotIndex + 1);
    }
}
