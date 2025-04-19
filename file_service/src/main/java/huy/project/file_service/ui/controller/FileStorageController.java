package huy.project.file_service.ui.controller;

import huy.project.file_service.core.domain.dto.request.StoreFileClassificationDto;
import huy.project.file_service.core.domain.entity.FileResourceEntity;
import huy.project.file_service.core.service.IFileStorageService;
import huy.project.file_service.kernel.utils.AuthenUtils;
import huy.project.file_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/public/v1/file_storage")
@RequiredArgsConstructor
public class FileStorageController {
    private final IFileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Resource<List<FileResourceEntity>>> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files)
    {
        Long userId = AuthenUtils.getCurrentUserId();
        return ResponseEntity.ok(new Resource<>(fileStorageService.storeFiles(userId, files)));
    }

    @PostMapping("/upload/classified")
    public ResponseEntity<Resource<List<FileResourceEntity>>> uploadClassifiedFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("category") String category,
            @RequestParam(value = "referenceId", required = false) String referenceId,
            @RequestParam(value = "referenceType", required = false) String referenceType,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPublic", required = false) Boolean isPublic)
    {
        Long userId = AuthenUtils.getCurrentUserId();
        var storeFileClassification = StoreFileClassificationDto.builder()
                .category(category)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .tags(tags)
                .description(description)
                .isPublic(isPublic)
                .build();
        return ResponseEntity.ok(new Resource<>(fileStorageService.storeFilesWithClassification(
                userId, files, storeFileClassification)));
    }

    @GetMapping("/download/fileName/{fileName}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(
            @PathVariable String fileName)
    {
        var result = fileStorageService.downloadFile(fileName);
        var resource = result.getFirst();
        var fileResource = result.getSecond();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResource.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(
            @PathVariable Long id)
    {
        var result = fileStorageService.downloadFile(id);
        var resource = result.getFirst();
        var fileResource = result.getSecond();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileResource.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Resource<?>> deleteFile(
            @RequestParam(name = "ids") List<Long> ids)
    {
        Long userId = AuthenUtils.getCurrentUserId();
        fileStorageService.deleteFiles(userId, ids);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
