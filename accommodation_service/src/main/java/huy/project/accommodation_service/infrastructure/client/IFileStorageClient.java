package huy.project.accommodation_service.infrastructure.client;

import huy.project.accommodation_service.core.domain.dto.response.FileResourceResponse;
import huy.project.accommodation_service.kernel.config.FeignClientConfig;
import huy.project.accommodation_service.ui.resource.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(
        name = "file-service",
        url = "${app.services.file_service.url}",
        configuration = FeignClientConfig.class
)
public interface IFileStorageClient {
    @PostMapping(value = "api/public/v1/file_storage/upload/classified",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Resource<List<FileResourceResponse>> uploadMediaFile(
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam(value = "referenceId", required = false) String referenceId,
            @RequestParam(value = "referenceType", required = false) String referenceType
    );
}
