package huy.project.ai_service.core.port;

import org.springframework.web.multipart.MultipartFile;

public interface IChatPort {
    String getResponse(String prompt);

    String getResponseWithImage(String prompt, MultipartFile file);
}
