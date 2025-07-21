package huy.project.ai_service.core.port;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.multipart.MultipartFile;

public interface IChatPort {
    String getResponse(String prompt);

    String getResponseWithImage(String prompt, MultipartFile file);

    <T> T getStructureResponse(String prompt, Class<T> clazz);

    <T> T getStructureResponse(String prompt, ParameterizedTypeReference<T> typeReference);
}
