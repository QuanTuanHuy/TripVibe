package huy.project.file_service.core.service.impl;

import huy.project.file_service.core.domain.entity.FileResourceEntity;
import huy.project.file_service.core.service.IFileStorageService;
import huy.project.file_service.core.usecase.DeleteFileUseCase;
import huy.project.file_service.core.usecase.DownloadFileUseCase;
import huy.project.file_service.core.usecase.StoreFileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorageService implements IFileStorageService {
    private final StoreFileUseCase storeFileUseCase;
    private final DownloadFileUseCase downloadFileUseCase;
    private final DeleteFileUseCase deleteFileUseCase;

    @Override
    public List<FileResourceEntity> storeFiles(Long userId, MultipartFile[] files) {
        return storeFileUseCase.storeFiles(userId, files);
    }

    @Override
    public Pair<Resource, FileResourceEntity> downloadFile(Long id) {
        return downloadFileUseCase.downloadFile(id);
    }

    @Override
    public void deleteFiles(Long userId, List<Long> ids) {
        deleteFileUseCase.deleteFiles(userId, ids);
    }
}
