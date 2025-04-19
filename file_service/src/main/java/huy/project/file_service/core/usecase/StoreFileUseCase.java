package huy.project.file_service.core.usecase;

import huy.project.file_service.core.domain.constant.FileCategory;
import huy.project.file_service.core.domain.constant.ImageSize;
import huy.project.file_service.core.domain.dto.request.StoreFileClassificationDto;
import huy.project.file_service.core.domain.entity.FileResourceEntity;
import huy.project.file_service.core.port.IFileResourcePort;
import huy.project.file_service.core.port.IFileStoragePort;
import huy.project.file_service.core.processor.ImageProcessor;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StoreFileUseCase {
    IFileStoragePort fileStoragePort;
    IFileResourcePort fileResourcePort;
    ImageProcessor imageProcessor;

    @NonFinal
    @Value("${app.file-storage.download-url}")
    private String DOWNLOAD_URL;

    @Transactional(rollbackFor = Exception.class)
    public List<FileResourceEntity> storeFiles(Long userId, MultipartFile[] files) {
        List<FileResourceEntity> fileResources = new ArrayList<>();

        Arrays.stream(files).forEach(f -> {
            String fileName = fileStoragePort.storeFile(f);
            var fileResource = buildFileResource(f, fileName, userId);
            fileResources.add(fileResource);
        });

        var finalFileResources = fileResourcePort.saveAll(fileResources);

        finalFileResources.forEach(f -> f.setUrl(DOWNLOAD_URL + f.getId()));

        return fileResourcePort.saveAll(finalFileResources);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<FileResourceEntity> storeFilesWithClassification(
            Long userId, MultipartFile[] files, StoreFileClassificationDto storeFileClassificationDto) {
        List<FileResourceEntity> fileResources = new ArrayList<>();

        String groupId = UUID.randomUUID().toString();
        storeFileClassificationDto.setGroupId(groupId);

        for (var f : files) {
            if (f.isEmpty()) {
                log.error("File is empty");
                continue;
            }

            if (!imageProcessor.isImage(f.getContentType())) {
                String fileName = fileStoragePort.storeFile(f);
                var fileResource = buildFileResource(
                        f, fileName, userId, storeFileClassificationDto, null, null, null
                );
                fileResources.add(fileResource);
            } else {
                // Đọc ảnh và lấy metadata
                try {
                    var originalImage = ImageIO.read(f.getInputStream());
                    int width = originalImage.getWidth();
                    int height = originalImage.getHeight();

                    // Xử lý file và tạo các phiên bản
                    Map<ImageSize, byte[]> imageVersions = imageProcessor.processImage(f);

                    // Tạo tên file ngẫu nhiên để tránh trùng lặp
                    String baseFileName = UUID.randomUUID().toString();
                    String extension = StringUtils.getFilenameExtension(f.getOriginalFilename());
                    log.info("original file name: {}", f.getOriginalFilename());

                    // Lưu các phiên bản xuống đĩa
                    Map<String, String> versionUrls = new HashMap<>();

                    for (Map.Entry<ImageSize, byte[]> entry : imageVersions.entrySet()) {
                        ImageSize size = entry.getKey();
                        byte[] data = entry.getValue();

                        // Tạo tên file cho phiên bản này
                        String fileName = baseFileName;
                        if (size != ImageSize.ORIGINAL) {
                            fileName += "_" + size.getSuffix();
                        }
                        fileName += "." + extension;

                        // Lưu file
                        fileName = fileStoragePort.storeFile(fileName, data);

                        // Lưu URL
                        versionUrls.put(size.name(), fileName);

                    }
                    // Lưu thông tin vào file resource
                    var fileResource = buildFileResource(f, baseFileName + "." + extension, userId, storeFileClassificationDto, versionUrls, width, height);
                    fileResources.add(fileResource);

                } catch (Exception e) {
                    log.error("Error processing image: {}", e.getMessage());
                }

            }
        }

        if (!CollectionUtils.isEmpty(fileResources)) {
            var finalFileResources = fileResourcePort.saveAll(fileResources);

            finalFileResources.forEach(f -> f.setUrl(DOWNLOAD_URL + f.getId()));

            return fileResourcePort.saveAll(finalFileResources);
        } else {
            return List.of();
        }
    }

    private FileResourceEntity buildFileResource(
            MultipartFile file, String fileName, Long userId, StoreFileClassificationDto dto,
            Map<String, String> versions, Integer width, Integer height) {
        String tagsString = dto.getTags() != null && dto.getTags().length > 0 ? String.join(",", dto.getTags()) : null;

        return FileResourceEntity.builder()
                .fileName(fileName)
                .originalName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())))
                .contentType(file.getContentType())
                .size(file.getSize())
                .createdBy(userId)
                // image
                .isImage(imageProcessor.isImage(file.getContentType()))
                .width(width)
                .height(height)
                .versions(versions)
                // Thông tin phân loại
                .category(StringUtils.hasText(dto.getCategory()) ? FileCategory.valueOf(dto.getCategory()).getCode() : null)
                .referenceId(dto.getReferenceId())
                .referenceType(dto.getReferenceType())
                .tags(tagsString)
                .description(dto.getDescription())
                .isPublic(dto.getIsPublic() != null ? dto.getIsPublic() : true)
                .groupId(dto.getGroupId())
                // Thông tin ảnh
                .width(width)
                .height(height)
                .build();
    }

    private FileResourceEntity buildFileResource(MultipartFile file, String url, Long userId) {
        return FileResourceEntity.builder()
                .fileName(url)
                .originalName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())))
                .contentType(file.getContentType())
                .size(file.getSize())
                .createdBy(userId)
                .isPublic(true)
                .build();
    }
}
