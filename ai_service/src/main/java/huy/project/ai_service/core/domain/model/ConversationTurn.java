package huy.project.ai_service.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationTurn {
    private String id;
    private String conversationId;
    private String userMessage;
    private String aiResponse;
    private LocalDateTime timestamp;
    private int tokenCount;
    private Map<String, Object> metadata;
    
    public int getTotalTokens() {
        return estimateTokens(userMessage) + estimateTokens(aiResponse);
    }
    
    private int estimateTokens(String text) {
        if (text == null) return 0;
        // Rough estimation: ~4 characters per token for Vietnamese/English
        return (int) Math.ceil(text.length() / 4.0);
    }
}
