package huy.project.file_service.infrastructure.repository.adapter;

import huy.project.file_service.core.domain.entity.FileResourceEntity;
import huy.project.file_service.core.port.IFileResourcePort;
import huy.project.file_service.infrastructure.repository.IFileResourceRepository;
import huy.project.file_service.infrastructure.repository.mapper.FileResourceMapper;
import huy.project.file_service.infrastructure.repository.model.FileResourceModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileResourceAdapter implements IFileResourcePort {
    IFileResourceRepository fileResourceRepository;
    FileResourceMapper fileResourceMapper;

    @Override
    public List<FileResourceEntity> saveAll(List<FileResourceEntity> fileResources) {
        var fileResourceModels = fileResources.stream()
                .map(fileResourceMapper::toModel).toList();
        return fileResourceRepository.saveAll(fileResourceModels).stream()
                .map(fileResourceMapper::toEntity).toList();
    }

    @Override
    public FileResourceEntity getFileResourceById(Long id) {
        return fileResourceMapper.toEntity(fileResourceRepository.findById(id).orElse(null));
    }

    @Override
    public FileResourceEntity getFileResourceByName(String fileName) {
        List<FileResourceModel> files = fileResourceRepository.findByVersionsContaining(fileName).stream()
                .distinct().toList();
        if (CollectionUtils.isEmpty(files)) {
            return null;
        }
        return fileResourceMapper.toEntity(files.get(0));
    }

    @Override
    public void deleteFileById(Long id) {
        fileResourceRepository.deleteById(id);
    }
}
