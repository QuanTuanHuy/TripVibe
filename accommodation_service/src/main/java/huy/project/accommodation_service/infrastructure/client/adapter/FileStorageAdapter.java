package huy.project.accommodation_service.infrastructure.client.adapter;

import huy.project.accommodation_service.core.domain.constant.ErrorCode;
import huy.project.accommodation_service.core.domain.dto.response.FileResourceResponse;
import huy.project.accommodation_service.core.exception.AppException;
import huy.project.accommodation_service.core.port.IFileStoragePort;
import huy.project.accommodation_service.infrastructure.client.IFileStorageClient;
import huy.project.accommodation_service.kernel.utils.JsonUtils;
import huy.project.accommodation_service.ui.resource.Resource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FileStorageAdapter implements IFileStoragePort {
    IFileStorageClient fileStorageClient;
    JsonUtils jsonUtils;

    @Override
    public List<FileResourceResponse> uploadFiles(List<MultipartFile> files, String referenceId, String referenceType) {
        try {
            Resource<List<FileResourceResponse>> response = fileStorageClient.uploadMediaFile(
                    files, referenceId, referenceType);
            if (!response.getMeta().getMessage().equals("Success")) {
                log.error("Error uploading files: {}", response.getMeta().getMessage());
                return null;
            }
            log.info("Upload files to server successfully: {}", jsonUtils.toJson(response.getData()));
            return response.getData();
        } catch (Exception e) {
            log.error("Error when uploading files to server: {}", e.getMessage());
            throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }
}
