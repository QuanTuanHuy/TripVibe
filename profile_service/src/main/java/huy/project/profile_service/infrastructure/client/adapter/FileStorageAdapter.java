package huy.project.profile_service.infrastructure.client.adapter;

import huy.project.profile_service.core.domain.constant.ErrorCode;
import huy.project.profile_service.core.domain.dto.response.FileResourceResponse;
import huy.project.profile_service.core.domain.exception.AppException;
import huy.project.profile_service.core.port.client.IFileStoragePort;
import huy.project.profile_service.infrastructure.client.IFileStorageClient;
import huy.project.profile_service.ui.resource.Resource;
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

    @Override
    public List<FileResourceResponse> uploadFiles(List<MultipartFile> files) {
        try {
            Resource<List<FileResourceResponse>> response = fileStorageClient.uploadMultipleFiles(files);
            if (response.getMeta().getMessage().equals("Success")) {
                return response.getData();
            } else {
                log.error("Failed to upload files: {}", response.getMeta().getMessage());
                throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }
}
