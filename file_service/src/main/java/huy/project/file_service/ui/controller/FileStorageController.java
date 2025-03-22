package huy.project.file_service.ui.controller;

import huy.project.file_service.core.domain.entity.FileResourceEntity;
import huy.project.file_service.core.service.IFileStorageService;
import huy.project.file_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/public/v1/file_storage")
@RequiredArgsConstructor
public class FileStorageController {
    private final IFileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Resource<FileResourceEntity>> uploadFile(
            @RequestParam("file") MultipartFile file)
    {
        Long userId = 1L;
        return ResponseEntity.ok(new Resource<>(fileStorageService.storeFile(userId, file)));
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
}
