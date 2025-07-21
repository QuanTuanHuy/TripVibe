package huy.project.ai_service.core.service;

import huy.project.ai_service.core.domain.dto.request.ChatRequest;
import org.springframework.web.multipart.MultipartFile;

public interface IChatService {
    String chat(ChatRequest request);

    String chatWithImage(MultipartFile file, String message);
}
