package huy.project.ai_service.core.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RAGChatResponse {
    private String response;
    private List<SourceDocument> sources;
    private String conversationId;
    private Integer contextUsed;
    private Long processingTimeMs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SourceDocument {
        private String documentId;
        private String filename;
        private String content;
        private double similarity;
        private Map<String, Object> metadata;
    }
}

