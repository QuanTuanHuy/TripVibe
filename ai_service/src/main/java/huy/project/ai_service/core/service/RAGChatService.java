package huy.project.ai_service.core.service;

import huy.project.ai_service.core.domain.dto.request.RAGChatRequest;
import huy.project.ai_service.core.domain.dto.response.RAGChatResponse;
import huy.project.ai_service.core.tool.ToolManager;
import huy.project.ai_service.kernel.properties.RAGProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RAGChatService implements IRAGChatService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory;

    private final ToolManager toolManager;

    private final RAGProperty ragProperty;

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
            // 1. search relevant documents
            List<Document> relevantDocs = searchRelevantDocuments(request);

            // 2. build context from documents
            String context = buildContext(relevantDocs);

            // 3. get conversation history
            String conversationId = getOrCreateConversationId(request.getConversationId());
            List<Message> messages = getConversationMessages(conversationId);
            String history = formatMessagesAsHistory(messages);

            // 4. generate response
            String response = generateResponse(request.getMessage(), context, history);

            // 5. save conversation history
            saveConversationMessages(conversationId, request.getMessage(), response);

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
                    .topK(request.getMaxResults() > 0 ? request.getMaxResults() : ragProperty.getMaxResults())
                    .similarityThreshold(request.getSimilarityThreshold() > 0 ? request.getSimilarityThreshold()
                            : ragProperty.getSimilarityThreshold());

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
                    .similarityThreshold(ragProperty.getSimilarityThreshold())
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

    private List<Message> getConversationMessages(String conversationId) {
        if (conversationId == null) {
            return Collections.emptyList();
        }

        try {
            List<Message> messages = chatMemory.get(conversationId);
            log.info("Found {} messages for conversation: {}", messages.size(), conversationId);
            return messages;
        } catch (Exception e) {
            log.error("Error retrieving conversation messages for ID: {}", conversationId, e);
            return Collections.emptyList();
        }
    }

    private void saveConversationMessages(String conversationId, String question, String response) {
        if (conversationId == null) {
            return;
        }

        try {
            UserMessage usermessage = new UserMessage(question);
            chatMemory.add(conversationId, usermessage);

            AssistantMessage assistantMessage = new AssistantMessage(response);
            chatMemory.add(conversationId, assistantMessage);

            log.info("Saved conversation messages for ID: {}", conversationId);
        } catch (Exception e) {
            log.error("Error saving conversation messages for ID: {}", conversationId, e);
        }
    }

    private String formatMessagesAsHistory(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return "Chưa có lịch sử trò chuyện.";
        }

        StringBuilder history = new StringBuilder();

        for (var message : messages) {
            String role = switch (message.getMessageType()) {
                case USER -> "Người dùng";
                case ASSISTANT -> "AI";
                case SYSTEM -> "Hệ thống";
                default -> "Khác";
            };

            history.append(role).append(": ").append(message.getText()).append("\n");
        }

        return history.toString().trim();
    }
}
