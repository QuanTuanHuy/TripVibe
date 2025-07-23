package huy.project.ai_service.core.service;

import huy.project.ai_service.core.domain.dto.request.RAGChatRequest;
import huy.project.ai_service.core.domain.dto.response.RAGChatResponse;

import java.util.List;

public interface IRAGChatService {
    RAGChatResponse chat(RAGChatRequest request);

    String simpleChat(String message);

    List<RAGChatResponse.SourceDocument> searchSimilarDocuments(
            String query, int maxResults
    );
}
