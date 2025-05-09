package huy.project.profile_service.infrastructure.client;

import huy.project.profile_service.core.domain.dto.response.FileResourceResponse;
import huy.project.profile_service.kernel.config.FeignClientConfig;
import huy.project.profile_service.ui.resource.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(
        name = "accommodation-service",
        url = "${app.services.file_service.url}",
        configuration = FeignClientConfig.class
)
public interface IFileStorageClient {
    @PostMapping("/api/public/v1/file_storage/upload")
    Resource<List<FileResourceResponse>> uploadMultipleFiles(
            @RequestParam("files") List<MultipartFile> files);
}
