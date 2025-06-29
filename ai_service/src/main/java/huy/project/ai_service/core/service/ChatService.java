package huy.project.ai_service.core.service;

import huy.project.ai_service.core.dto.request.ChatRequest;
import huy.project.ai_service.core.port.IChatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService implements IChatService{
    private final IChatPort chatPort;

    @Override
    public String chat(ChatRequest request) {
        return chatPort.getResponse(request.getMessage());
    }
}
