package huy.project.profile_service.infrastructure.client;

import huy.project.profile_service.core.domain.dto.response.FileResourceResponse;
import huy.project.profile_service.kernel.config.FeignClientConfig;
import huy.project.profile_service.ui.resource.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(
        name = "file-service",
        url = "${app.services.file_service.url}",
        configuration = FeignClientConfig.class
)
public interface IFileStorageClient {
    @PostMapping(value = "/api/public/v1/file_storage/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Resource<List<FileResourceResponse>> uploadMultipleFiles(
            @RequestPart("files") List<MultipartFile> files);
}
