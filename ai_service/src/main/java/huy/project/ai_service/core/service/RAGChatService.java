package huy.project.ai_service.core.service;

import huy.project.ai_service.core.domain.dto.request.RAGChatRequest;
import huy.project.ai_service.core.domain.dto.response.RAGChatResponse;
import huy.project.ai_service.core.tool.ToolManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class RAGChatService implements IRAGChatService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final Map<String, List<String>> conversationHistory = new ConcurrentHashMap<>();

    private final ToolManager toolManager;

    @Value("${rag.max-results}")
    private int maxResults;

    @Value("${rag.similarity-threshold}")
    private double similarityThreshold;

    // private static final String RAG_PROMPT_TEMPLATE = """
    // Bạn là một AI assistant thông minh và hữu ích cho hệ thống đặt phòng khách
    // sạn.
    //
    // Hãy trả lời câu hỏi của người dùng dựa trên thông tin được cung cấp dưới đây.
    // Nếu thông tin không đủ để trả lời, hãy nói rõ và đề xuất cách tìm thêm thông
    // tin.
    //
    // NGUYÊN TẮC:
    // 1. Chỉ sử dụng thông tin từ context được cung cấp
    // 2. Trả lời chính xác, ngắn gọn và hữu ích
    // 3. Nếu không chắc chắn, hãy thừa nhận và gợi ý
    // 4. Luôn thân thiện và chuyên nghiệp
    //
    // CONTEXT:
    // {context}
    //
    // LỊCH SỬ TRƯỚC ĐÓ:
    // {history}
    //
    // CÂU HỎI: {question}
    //
    // TRẢ LỜI:
    // """;
    private static final String RAG_PROMPT_TEMPLATE = """
            Bạn là một AI assistant thông minh và hữu ích cho nhiệm vụ tìm kiếm thông tin từ tài liệu công nghệ.
            
            Hãy trả lời câu hỏi của người dùng dựa trên thông tin được cung cấp dưới đây.
            Nếu thông tin không đủ để trả lời, hãy nói rõ và đề xuất cách tìm thêm thông tin.
            
            NGUYÊN TẮC:
            1. Chỉ sử dụng thông tin từ context được cung cấp
            2. Trả lời chính xác, ngắn gọn và hữu ích
            3. Nếu không chắc chắn, hãy thừa nhận và gợi ý
            4. Luôn thân thiện và chuyên nghiệp
            
            CONTEXT:
            {context}
            
            LỊCH SỬ TRƯỚC ĐÓ:
            {history}
            
            CÂU HỎI: {question}
            
            TRẢ LỜI:
            """;

    private static final String SIMPLE_CHAT_TEMPLATE = """
            Bạn là một AI assistant thông minh cho hệ thống đặt phòng khách sạn.
            Hãy trả lời câu hỏi của người dùng một cách thân thiện và hữu ích.
            
            CÂU HỎI: {question}
            
            TRẢ LỜI:
            """;

    @Override
    public RAGChatResponse chat(RAGChatRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            List<Document> relevantDocs = searchRelevantDocuments(request);

            String context = buildContext(relevantDocs);
            String history = getConversationHistory(request.getConversationId());

            String response = generateResponse(request.getMessage(), context, history);

            saveConversationHistory(request.getConversationId(), request.getMessage(), response);

            List<RAGChatResponse.SourceDocument> sources = buildSourceDocuments(relevantDocs,
                    request.isIncludeSources());

            long processingTime = System.currentTimeMillis() - startTime;

            return RAGChatResponse.builder()
                    .response(response)
                    .sources(sources)
                    .conversationId(getOrCreateConversationId(request.getConversationId()))
                    .contextUsed(relevantDocs.size())
                    .processingTimeMs(processingTime)
                    .build();

        } catch (Exception e) {
            log.error("Error in RAG chat", e);
            throw new RuntimeException("Failed to process RAG chat: " + e.getMessage());
        }
    }

    private String getConversationHistory(String conversationId) {
        if (conversationId == null) {
            return "Chưa có lịch sử trò chuyện.";
        }

        List<String> history = conversationHistory.get(conversationId);
        if (history == null || history.isEmpty()) {
            return "Chưa có lịch sử trò chuyện.";
        }

        int startIndex = Math.max(0, history.size() - 10);
        return String.join("\n", history.subList(startIndex, history.size()));
    }

    private void saveConversationHistory(String conversationId, String question, String response) {
        if (conversationId == null) {
            return;
        }

        conversationHistory.computeIfAbsent(conversationId, k -> new ArrayList<>());
        List<String> history = conversationHistory.get(conversationId);

        history.add("Người dùng: " + question);
        history.add("AI: " + response);

        if (history.size() > 50) {
            history.subList(0, history.size() - 50).clear();
        }
    }

    private String getOrCreateConversationId(String conversationId) {
        return conversationId != null ? conversationId : UUID.randomUUID().toString();
    }

    private String generateResponse(String question, String context, String history) {
        PromptTemplate promptTemplate = new PromptTemplate(RAG_PROMPT_TEMPLATE);

        Map<String, Object> promptVariables = Map.of(
                "question", question,
                "context", context,
                "history", history);

        return chatClient.prompt()
                .user(promptTemplate.render(promptVariables))
                .toolCallbacks(toolManager.getAllTools())
                .call()
                .content();
    }

    private List<Document> searchRelevantDocuments(RAGChatRequest request) {
        try {
            SearchRequest.Builder searchRequestBuilder = SearchRequest.builder()
                    .query(request.getMessage())
                    .topK(request.getMaxResults() > 0 ? request.getMaxResults() : maxResults)
                    .similarityThreshold(request.getSimilarityThreshold() > 0 ? request.getSimilarityThreshold()
                            : similarityThreshold);

            if (request.getFilters() != null && !request.getFilters().isEmpty()) {
                Filter.Expression filterExpression = buildFilterExpression(request.getFilters());
                searchRequestBuilder.filterExpression(filterExpression);
            }

            SearchRequest searchRequest = searchRequestBuilder.build();

            List<Document> documents = vectorStore.similaritySearch(searchRequest);

            log.info("Found {} relevant documents for query: {}",
                    !CollectionUtils.isEmpty(documents) ? documents.size() : 0,
                    request.getMessage());

            return documents;
        } catch (Exception e) {
            log.error("Error searching relevant documents for query: {}", request.getMessage(), e);
            throw new RuntimeException("Error searching relevant documents", e);
        }
    }

    private Filter.Expression buildFilterExpression(Map<String, String> filters) {
        FilterExpressionBuilder builder = new FilterExpressionBuilder();

        Filter.Expression expression = null;

        for (Map.Entry<String, String> filter : filters.entrySet()) {
            Filter.Expression current = builder.eq(filter.getKey(), filter.getValue()).build();

            if (expression == null) {
                expression = current;
            } else {
                expression = builder.and(
                                new FilterExpressionBuilder.Op(expression),
                                new FilterExpressionBuilder.Op(current))
                        .build();
            }
        }

        return expression;
    }

    private String buildContext(List<Document> documents) {
        if (documents.isEmpty()) {
            return "Không có thông tin liên quan được tìm thấy.";
        }

        StringBuilder context = new StringBuilder();

        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            context.append("--- Tài liệu ").append(i + 1).append(" ---\n");
            context.append("Nguồn: ").append(doc.getMetadata().get("filename")).append("\n");
            context.append("Nội dung: ").append(doc.getText()).append("\n\n");
        }

        return context.toString();
    }

    @Override
    public String simpleChat(String message) {
        try {
            PromptTemplate promptTemplate = PromptTemplate.builder()
                    .template(SIMPLE_CHAT_TEMPLATE)
                    .build();
            Map<String, Object> variables = Map.of("question", message);

            return chatClient.prompt()
                    .user(promptTemplate.render(variables))
                    .toolCallbacks(toolManager.getAllTools())
                    .call()
                    .content();
        } catch (Exception e) {
            throw new RuntimeException("Error during simple chat", e);
        }
    }

    @Override
    public List<RAGChatResponse.SourceDocument> searchSimilarDocuments(String query, int maxResults) {
        try {
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(query)
                    .topK(maxResults)
                    .similarityThreshold(similarityThreshold)
                    .build();

            List<Document> documents = vectorStore.similaritySearch(searchRequest);
            if (CollectionUtils.isEmpty(documents)) {
                log.info("No similar documents found for query: {}", query);
                return Collections.emptyList();
            }
            return buildSourceDocuments(documents, true);
        } catch (Exception e) {
            throw new RuntimeException("Error during search similar documents", e);
        }
    }

    private List<RAGChatResponse.SourceDocument> buildSourceDocuments(List<Document> documents, boolean includeSource) {
        return documents.stream()
                .map(doc -> RAGChatResponse.SourceDocument.builder()
                        .documentId((String) doc.getMetadata().get("document_id"))
                        .filename((String) doc.getMetadata().get("filename"))
                        .content(includeSource ? doc.getText() : null)
                        .similarity(doc.getScore() != null ? doc.getScore() : 1.0)
                        .metadata(doc.getMetadata())
                        .build())
                .toList();
    }
}
