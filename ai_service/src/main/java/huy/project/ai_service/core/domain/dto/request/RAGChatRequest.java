package huy.project.ai_service.core.domain.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class RAGChatRequest {
    private String message;
    private int maxResults = 5;
    private double similarityThreshold = 0.7;
    private Map<String, String> filters;
    private boolean includeSources = true;
    private String conversationId;
}
