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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public List<FileResourceEntity> storeFiles(Long userId, MultipartFile[] files) {
        List<FileResourceEntity> fileResources = new ArrayList<>();

        Arrays.stream(files).forEach(f -> {
            String url = fileStoragePort.storeFile(f);
            var fileResource = buildFileResource(f, url, userId);
            fileResources.add(fileResource);
        });

        fileResourcePort.saveAll(fileResources);

        fileResources.forEach(f -> f.setUrl(DOWNLOAD_URL + f.getId()));

        return fileResourcePort.saveAll(fileResources);
    }

    private FileResourceEntity buildFileResource(MultipartFile file, String url, Long userId) {
        return FileResourceEntity.builder()
                .fileName(url)
                .originalName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())))
                .contentType(file.getContentType())
                .size(file.getSize())
                .createdBy(userId)
                .build();
    }
}
