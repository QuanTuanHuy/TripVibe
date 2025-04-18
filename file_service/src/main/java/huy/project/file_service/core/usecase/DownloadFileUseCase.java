package huy.project.file_service.core.usecase;

import huy.project.file_service.core.domain.constant.ErrorCode;
import huy.project.file_service.core.domain.entity.FileResourceEntity;
import huy.project.file_service.core.exception.AppException;
import huy.project.file_service.core.port.IFileResourcePort;
import huy.project.file_service.core.port.IFileStoragePort;
import huy.project.file_service.kernel.utils.AuthenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DownloadFileUseCase {
    private final IFileStoragePort fileStoragePort;
    private final IFileResourcePort fileResourcePort;

    public Pair<Resource, FileResourceEntity> downloadFile(Long id) {
        var fileResource = fileResourcePort.getFileResourceById(id);

        if (fileResource == null) {
            log.error("File resource not found, id: {}", id);
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }

        if (!fileResource.getIsPublic()) {
            // check if user is allowed to download
            Long userId = AuthenUtils.getCurrentUserId();
            if (!fileResource.getCreatedBy().equals(userId)) {
                log.error("User {} is not allowed to download file {}", userId, id);
                throw new AppException(ErrorCode.FORBIDDEN_DOWNLOAD_FILE);
            }
        }

        var path = fileStoragePort.getFilePath(fileResource.getFileName());
        try {
            var resource = new UrlResource(path.toUri());
            return Pair.of(resource, fileResource);
        } catch (MalformedURLException e) {
            log.error("Could not load file as resource", e);
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }
    }
}
