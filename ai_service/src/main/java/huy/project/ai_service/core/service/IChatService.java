package huy.project.ai_service.core.service;

import huy.project.ai_service.core.domain.dto.request.ChatRequest;
import huy.project.ai_service.core.domain.dto.response.AccommodationInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IChatService {
    String chat(ChatRequest request);

    String chatWithImage(MultipartFile file, String message);

    List<AccommodationInfo> chatWithStructureOutput(ChatRequest request);
}
