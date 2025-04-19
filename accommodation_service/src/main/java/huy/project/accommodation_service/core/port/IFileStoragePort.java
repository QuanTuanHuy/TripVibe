package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.dto.response.FileResourceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileStoragePort {
    List<FileResourceResponse> uploadFiles(List<MultipartFile> files, String referenceId, String referenceType);
}
