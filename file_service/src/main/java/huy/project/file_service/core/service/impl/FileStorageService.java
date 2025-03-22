package huy.project.file_service.core.service.impl;

import huy.project.file_service.core.domain.entity.FileResourceEntity;
import huy.project.file_service.core.service.IFileStorageService;
import huy.project.file_service.core.usecase.DownloadFileUseCase;
import huy.project.file_service.core.usecase.StoreFileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileStorageService implements IFileStorageService {
    private final StoreFileUseCase storeFileUseCase;
    private final DownloadFileUseCase downloadFileUseCase;

    @Override
    public FileResourceEntity storeFile(Long userId, MultipartFile file) {
        return storeFileUseCase.storeFile(userId, file);
    }

    @Override
    public Pair<Resource, FileResourceEntity> downloadFile(Long id) {
        return downloadFileUseCase.downloadFile(id);
    }
}
