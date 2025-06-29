package huy.project.ai_service.core.service;

import huy.project.ai_service.core.dto.request.ChatRequest;

public interface IChatService {
    String chat(ChatRequest request);
}
