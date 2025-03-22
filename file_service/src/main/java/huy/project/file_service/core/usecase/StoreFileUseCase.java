package huy.project.file_service.core.usecase;

import huy.project.file_service.core.domain.entity.FileResourceEntity;
import huy.project.file_service.core.port.IFileResourcePort;
import huy.project.file_service.core.port.IFileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreFileUseCase {
    private final IFileStoragePort fileStoragePort;
    private final IFileResourcePort fileResourcePort;

    @Value("${app.file-storage.download-url}")
    private String DOWNLOAD_URL;

    @Transactional(rollbackFor = Exception.class)
    public FileResourceEntity storeFile(Long userId, MultipartFile file) {
        String url = fileStoragePort.storeFile(file);
        var fileResource = FileResourceEntity.builder()
                .fileName(url)
                .originalName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())))
                .contentType(file.getContentType())
                .size(file.getSize())
                .createdBy(userId)
                .build();
        fileResource = fileResourcePort.save(fileResource);
        fileResource.setUrl(DOWNLOAD_URL + fileResource.getId());
        return fileResourcePort.save(fileResource);
    }
}
