package huy.project.profile_service.core.port.client;

import huy.project.profile_service.core.domain.dto.response.FileResourceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileStoragePort {
    List<FileResourceResponse> uploadFiles(List<MultipartFile> files);
}
