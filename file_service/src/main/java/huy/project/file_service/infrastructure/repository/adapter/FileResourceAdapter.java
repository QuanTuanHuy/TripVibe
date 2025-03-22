package huy.project.file_service.infrastructure.repository.adapter;

import huy.project.file_service.core.domain.entity.FileResourceEntity;
import huy.project.file_service.core.port.IFileResourcePort;
import huy.project.file_service.infrastructure.repository.IFileResourceRepository;
import huy.project.file_service.infrastructure.repository.mapper.FileResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileResourceAdapter implements IFileResourcePort {
    private final IFileResourceRepository fileResourceRepository;

    @Override
    public FileResourceEntity save(FileResourceEntity fileResource) {
        var fileResourceModel = FileResourceMapper.toModel(fileResource);
        return FileResourceMapper.toEntity(fileResourceRepository.save(fileResourceModel));
    }

    @Override
    public FileResourceEntity getFileResourceById(Long id) {
        return FileResourceMapper.toEntity(fileResourceRepository.findById(id).orElse(null));
    }
}
