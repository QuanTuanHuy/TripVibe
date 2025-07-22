# Cải Tiến Hệ Thống Advisor - Spring AI

## Tổng Quan

Dựa trên tài liệu Spring AI về Advisors API, đây là các đề xuất cải tiến cho hệ thống AI Service hiện tại để tận dụng tối đa khả năng của Spring AI Advisors.

## 1. Cải Tiến Architecture hiện tại

### 1.1 Thay thế kiến trúc hiện tại bằng Advisor Pattern

**Vấn đề hiện tại:**
- Code trong `RAGChatService` xử lý nhiều concern khác nhau (memory, RAG, tools) trong một method
- Khó mở rộng và maintain
- Không tái sử dụng được các component

**Giải pháp:**
```java
@Service
public class ModularRAGChatService implements IRAGChatService {
    
    private final ChatClient chatClient;
    
    public ModularRAGChatService(ChatModel chatModel, 
                               QuestionAnswerAdvisor qaAdvisor,
                               MessageChatMemoryAdvisor memoryAdvisor,
                               SafeGuardAdvisor safetyAdvisor,
                               CustomLoggingAdvisor loggingAdvisor) {
        this.chatClient = ChatClient.builder(chatModel)
            .defaultAdvisors(
                loggingAdvisor,           // Order 1: Logging đầu tiên
                safetyAdvisor,            // Order 2: Safety check
                memoryAdvisor,            // Order 3: Memory management  
                qaAdvisor                 // Order 4: RAG cuối cùng
            )
            .build();
    }
    
    @Override
    public RAGChatResponse chat(RAGChatRequest request) {
        return chatClient.prompt()
            .user(request.getMessage())
            .advisors(a -> {
                a.param(ChatMemory.CONVERSATION_ID, request.getConversationId());
                a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, buildFilterExpression(request.getFilters()));
            })
            .call()
            .entity(RAGChatResponse.class);
    }
}
```

## 2. Custom Advisors cần triển khai

### 2.1 Performance Monitoring Advisor
```java
@Component
public class PerformanceMonitoringAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    
    private final MeterRegistry meterRegistry;
    private final Logger logger = LoggerFactory.getLogger(PerformanceMonitoringAdvisor.class);
    
    @Override
    public String getName() {
        return "PerformanceMonitoringAdvisor";
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // Chạy đầu tiên
    }
    
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        Timer.Sample sample = Timer.start(meterRegistry);
        long startTime = System.currentTimeMillis();
        
        try {
            AdvisedResponse response = chain.nextAroundCall(advisedRequest);
            long duration = System.currentTimeMillis() - startTime;
            
            // Log performance metrics
            logger.info("Chat request processed in {}ms", duration);
            sample.stop(Timer.builder("chat.request.duration")
                .description("Chat request processing time")
                .register(meterRegistry));
                
            return response;
        } catch (Exception e) {
            logger.error("Error in chat request processing", e);
            throw e;
        }
    }
}
```

### 2.2 Content Safety Advisor (nâng cao)
```java
@Component
public class ContentSafetyAdvisor implements CallAroundAdvisor {
    
    private final List<String> bannedKeywords;
    private final ContentModerationService moderationService;
    
    @Override
    public String getName() {
        return "ContentSafetyAdvisor";
    }
    
    @Override
    public int getOrder() {
        return -100; // Chạy sớm để kiểm tra content
    }
    
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        String userMessage = advisedRequest.userText();
        
        // Kiểm tra nội dung không phù hợp
        if (containsInappropriateContent(userMessage)) {
            return AdvisedResponse.from(advisedRequest)
                .chatResponse(new ChatResponse(List.of(new Generation(
                    new AssistantMessage("Xin lỗi, tôi không thể xử lý yêu cầu này do vi phạm chính sách nội dung.")
                ))))
                .build();
        }
        
        AdvisedResponse response = chain.nextAroundCall(advisedRequest);
        
        // Kiểm tra response từ AI
        if (containsInappropriateContent(response.chatResponse().getResult().getOutput().getText())) {
            return AdvisedResponse.from(response)
                .chatResponse(new ChatResponse(List.of(new Generation(
                    new AssistantMessage("Xin lỗi, tôi không thể cung cấp thông tin này.")
                ))))
                .build();
        }
        
        return response;
    }
    
    private boolean containsInappropriateContent(String content) {
        return bannedKeywords.stream()
            .anyMatch(keyword -> content.toLowerCase().contains(keyword.toLowerCase()));
    }
}
```

### 2.3 Context Enhancement Advisor
```java
@Component
public class ContextEnhancementAdvisor implements CallAroundAdvisor {
    
    private final UserContextService userContextService;
    
    @Override
    public String getName() {
        return "ContextEnhancementAdvisor";
    }
    
    @Override
    public int getOrder() {
        return -50; // Chạy trước RAG advisor
    }
    
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        // Lấy context của user từ session/database
        String conversationId = (String) advisedRequest.adviseContext()
            .get(ChatMemory.CONVERSATION_ID);
            
        UserContext userContext = userContextService.getUserContext(conversationId);
        
        // Enhance prompt với user context
        String enhancedPrompt = enhancePromptWithContext(advisedRequest.userText(), userContext);
        
        AdvisedRequest enhancedRequest = AdvisedRequest.from(advisedRequest)
            .userText(enhancedPrompt)
            .updateContext(context -> {
                context.put("userPreferences", userContext.getPreferences());
                context.put("userLocation", userContext.getLocation());
                return context;
            })
            .build();
            
        return chain.nextAroundCall(enhancedRequest);
    }
    
    private String enhancePromptWithContext(String originalPrompt, UserContext context) {
        return String.format("""
            Thông tin người dùng:
            - Vị trí: %s
            - Sở thích: %s
            - Ngôn ngữ ưa thích: %s
            
            Câu hỏi: %s
            
            Hãy trả lời phù hợp với context của người dùng.
            """, 
            context.getLocation(),
            context.getPreferences(),
            context.getPreferredLanguage(),
            originalPrompt);
    }
}
```

### 2.4 Response Formatting Advisor
```java
@Component
public class ResponseFormattingAdvisor implements CallAroundAdvisor {
    
    @Override
    public String getName() {
        return "ResponseFormattingAdvisor";
    }
    
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE; // Chạy cuối cùng
    }
    
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        AdvisedResponse response = chain.nextAroundCall(advisedRequest);
        
        String originalResponse = response.chatResponse().getResult().getOutput().getText();
        String formattedResponse = formatResponse(originalResponse, advisedRequest.adviseContext());
        
        return AdvisedResponse.from(response)
            .chatResponse(new ChatResponse(List.of(new Generation(
                new AssistantMessage(formattedResponse)
            ))))
            .build();
    }
    
    private String formatResponse(String response, Map<String, Object> context) {
        // Format response dựa trên user preferences
        String userLanguage = (String) context.get("userLanguage");
        if ("en".equals(userLanguage)) {
            return formatForEnglish(response);
        }
        return formatForVietnamese(response);
    }
}
```

## 3. Cải tiến VectorStoreChatMemoryAdvisor

### 3.1 Custom Memory Strategy
```java
@Configuration
public class MemoryConfiguration {
    
    @Bean
    public VectorStoreChatMemoryAdvisor vectorStoreChatMemoryAdvisor(
            VectorStore vectorStore, 
            ChatModel chatModel) {
        
        return VectorStoreChatMemoryAdvisor.builder()
            .vectorStore(vectorStore)
            .chatClient(ChatClient.builder(chatModel).build())
            .searchRequest(SearchRequest.builder()
                .topK(5)
                .similarityThreshold(0.7)
                .build())
            .promptTemplate(createCustomMemoryPrompt())
            .build();
    }
    
    private PromptTemplate createCustomMemoryPrompt() {
        return PromptTemplate.builder()
            .template("""
                Bạn là một AI assistant thông minh cho hệ thống đặt phòng khách sạn.
                
                LỊCH SỬ CUỘC TRÒ CHUYỆN LIÊN QUAN:
                {long_term_memory}
                
                HƯỚNG DẪN HIỆN TẠI:
                {instructions}
                
                Hãy sử dụng lịch sử để hiểu ngữ cảnh và trả lời một cách nhất quán.
                """)
            .build();
    }
}
```

## 4. Dynamic Advisor Configuration

### 4.1 Runtime Advisor Management
```java
@Service
public class DynamicAdvisorService {
    
    private final ApplicationContext applicationContext;
    private final Map<String, Boolean> advisorStates = new ConcurrentHashMap<>();
    
    public ChatClient createChatClientWithAdvisors(RAGChatRequest request) {
        List<Advisor> advisors = new ArrayList<>();
        
        // Luôn có performance monitoring
        advisors.add(applicationContext.getBean(PerformanceMonitoringAdvisor.class));
        
        // Conditional advisors dựa trên request
        if (request.isEnableSafety()) {
            advisors.add(applicationContext.getBean(ContentSafetyAdvisor.class));
        }
        
        if (request.isEnableMemory()) {
            advisors.add(applicationContext.getBean(MessageChatMemoryAdvisor.class));
        }
        
        if (request.isEnableRAG()) {
            advisors.add(createRAGAdvisor(request));
        }
        
        return ChatClient.builder(chatModel)
            .defaultAdvisors(advisors.toArray(new Advisor[0]))
            .build();
    }
    
    private QuestionAnswerAdvisor createRAGAdvisor(RAGChatRequest request) {
        return QuestionAnswerAdvisor.builder(vectorStore)
            .searchRequest(SearchRequest.builder()
                .topK(request.getMaxResults())
                .similarityThreshold(request.getSimilarityThreshold())
                .build())
            .build();
    }
}
```

## 5. Advisor Chain Monitoring

### 5.1 Advisor Execution Tracing
```java
@Component
public class AdvisorTracingAdvisor implements CallAroundAdvisor {
    
    private final Tracer tracer;
    
    @Override
    public String getName() {
        return "AdvisorTracingAdvisor";
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
    
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        Span span = tracer.nextSpan()
            .name("advisor-chain-execution")
            .tag("user.message", advisedRequest.userText())
            .start();
            
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            AdvisedResponse response = chain.nextAroundCall(advisedRequest);
            
            span.tag("response.length", String.valueOf(
                response.chatResponse().getResult().getOutput().getText().length()));
            span.tag("success", "true");
            
            return response;
        } catch (Exception e) {
            span.tag("success", "false");
            span.tag("error", e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
```

## 6. Best Practices Implementation

### 6.1 Advisor Error Handling
```java
@Component
public class ErrorHandlingAdvisor implements CallAroundAdvisor {
    
    @Override
    public String getName() {
        return "ErrorHandlingAdvisor";
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
    
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        try {
            return chain.nextAroundCall(advisedRequest);
        } catch (Exception e) {
            logger.error("Error in advisor chain", e);
            
            // Return graceful error response
            return AdvisedResponse.from(advisedRequest)
                .chatResponse(new ChatResponse(List.of(new Generation(
                    new AssistantMessage("Xin lỗi, có lỗi xảy ra. Vui lòng thử lại sau.")
                ))))
                .build();
        }
    }
}
```

## 7. Migration Plan

### Phase 1: Foundation (Week 1-2)
1. Tạo các custom advisor cơ bản
2. Implement performance monitoring
3. Add basic error handling

### Phase 2: Core Features (Week 3-4)
1. Migrate RAG logic sang QuestionAnswerAdvisor
2. Implement custom memory advisor
3. Add content safety advisor

### Phase 3: Advanced Features (Week 5-6)
1. Dynamic advisor configuration
2. Context enhancement
3. Response formatting

### Phase 4: Optimization (Week 7-8)
1. Performance tuning
2. Monitoring và metrics
3. Documentation và testing

## 8. Benefits của việc migration

1. **Modularity**: Mỗi advisor độc lập, dễ test và maintain
2. **Reusability**: Có thể tái sử dụng advisors cho different chat clients
3. **Flexibility**: Dynamic configuration dựa trên request
4. **Observability**: Built-in tracing và monitoring
5. **Scalability**: Dễ thêm/bớt features mà không ảnh hưởng existing code

## 9. Configuration Example

```yaml
spring:
  ai:
    advisors:
      performance-monitoring:
        enabled: true
        order: -1000
      content-safety:
        enabled: true
        order: -100
        banned-keywords: ["inappropriate", "offensive"]
      memory:
        enabled: true
        order: -50
        max-history: 10
      rag:
        enabled: true
        order: 0
        max-results: 5
        similarity-threshold: 0.7
```

Với việc áp dụng Advisor Pattern, hệ thống sẽ trở nên modularity hơn, dễ maintain và extend hơn đáng kể.
