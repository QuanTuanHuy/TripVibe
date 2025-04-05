package huy.project.file_service.core.usecase;

import huy.project.file_service.core.domain.constant.ErrorCode;
import huy.project.file_service.core.exception.AppException;
import huy.project.file_service.core.port.IFileResourcePort;
import huy.project.file_service.core.port.IFileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteFileUseCase {
    private final IFileStoragePort fileStoragePort;
    private final IFileResourcePort fileResourcePort;

    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long userId, Long id) {
        var fileResource = fileResourcePort.getFileResourceById(id);
        if (fileResource == null) {
            log.error("File resource not found, id: {}", id);
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        if (!fileResource.getCreatedBy().equals(userId)) {
            log.error("User {} is not allowed to delete file {}", userId, id);
            throw new AppException(ErrorCode.FORBIDDEN_DELETE_FILE);
        }

        fileStoragePort.deleteFile(fileResource.getFileName());
        fileResourcePort.deleteFileById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteFiles(Long userId, List<Long> ids) {
        ids.forEach(i -> deleteFile(userId, i));
    }
}
