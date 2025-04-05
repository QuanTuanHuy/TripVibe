package huy.project.file_service.infrastructure.repository.adapter;

import huy.project.file_service.core.domain.entity.FileResourceEntity;
import huy.project.file_service.core.port.IFileResourcePort;
import huy.project.file_service.infrastructure.repository.IFileResourceRepository;
import huy.project.file_service.infrastructure.repository.mapper.FileResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileResourceAdapter implements IFileResourcePort {
    private final IFileResourceRepository fileResourceRepository;

    @Override
    public List<FileResourceEntity> saveAll(List<FileResourceEntity> fileResources) {
        var fileResourceModels = fileResources.stream()
                .map(FileResourceMapper::toModel).toList();
        return fileResourceRepository.saveAll(fileResourceModels).stream()
                .map(FileResourceMapper::toEntity).toList();
    }

    @Override
    public FileResourceEntity getFileResourceById(Long id) {
        return FileResourceMapper.toEntity(fileResourceRepository.findById(id).orElse(null));
    }

    @Override
    public void deleteFileById(Long id) {
        fileResourceRepository.deleteById(id);
    }
}
