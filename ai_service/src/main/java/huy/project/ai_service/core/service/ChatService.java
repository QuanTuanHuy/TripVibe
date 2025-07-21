package huy.project.ai_service.core.service;

import huy.project.ai_service.core.domain.dto.request.ChatRequest;
import huy.project.ai_service.core.domain.dto.response.AccommodationInfo;
import huy.project.ai_service.core.port.IChatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService implements IChatService{
    private final IChatPort chatPort;

    @Override
    public String chat(ChatRequest request) {
        return chatPort.getResponse(request.getMessage());
    }

    @Override
    public String chatWithImage(MultipartFile file, String message) {
        return chatPort.getResponseWithImage(message, file);
    }

    @Override
    public List<AccommodationInfo> chatWithStructureOutput(ChatRequest request) {
        return chatPort.getStructureResponse(
                request.getMessage(),
                new ParameterizedTypeReference<>() {
                });
    }
}
