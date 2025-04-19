package huy.project.file_service.infrastructure.repository;

import huy.project.file_service.infrastructure.repository.model.FileResourceModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFileResourceRepository extends IBaseRepository<FileResourceModel> {
    List<FileResourceModel> findByVersionsContaining(String fileName);
}
