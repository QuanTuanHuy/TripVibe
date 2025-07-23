# Cải Tiến Hệ Thống Memory Management - Spring AI

## Tổng Quan

Dựa trên tài liệu Spring AI về Chat Memory, đây là các đề xuất cải tiến toàn diện cho hệ thống quản lý memory trong AI service.

## 1. Current Memory System Analysis

### 1.1 Issues hiện tại
- Simple in-memory storage không persistent
- Không có memory strategy optimization
- Thiếu conversation context management
- Không support multiple memory types
- Limited scalability cho multiple users

### 1.2 Proposed Architecture

```java
@Configuration
@EnableConfigurationProperties(MemoryConfigurationProperties.class)
public class AdvancedMemoryConfiguration {
    
    @Bean
    @Primary
    public ChatMemoryRepository chatMemoryRepository(DataSource dataSource) {
        return JdbcChatMemoryRepository.builder()
            .jdbcTemplate(new JdbcTemplate(dataSource))
            .dialect(new PostgresChatMemoryDialect())
            .build();
    }
    
    @Bean
    public ChatMemory adaptiveChatMemory(ChatMemoryRepository repository) {
        return AdaptiveChatMemory.builder()
            .chatMemoryRepository(repository)
            .maxTokens(4000) // Based on model context window
            .compressionStrategy(new SemanticCompressionStrategy())
            .retentionStrategy(new ImportanceBasedRetentionStrategy())
            .build();
    }
    
    @Bean
    public VectorStoreChatMemoryAdvisor vectorMemoryAdvisor(VectorStore vectorStore, 
                                                          ChatModel chatModel) {
        return VectorStoreChatMemoryAdvisor.builder()
            .vectorStore(vectorStore)
            .chatClient(ChatClient.builder(chatModel).build())
            .searchRequest(SearchRequest.builder()
                .topK(5)
                .similarityThreshold(0.7)
                .build())
            .promptTemplate(createLongTermMemoryTemplate())
            .build();
    }
}
```

## 2. Advanced Memory Strategies

### 2.1 Adaptive Memory Management

```java
@Component
public class AdaptiveChatMemory implements ChatMemory {
    
    private final ChatMemoryRepository repository;
    private final MessageCompressionStrategy compressionStrategy;
    private final MessageRetentionStrategy retentionStrategy;
    private final TokenCounter tokenCounter;
    private final int maxTokens;
    
    @Override
    public void add(String conversationId, List<Message> messages) {
        // Store original messages
        repository.add(conversationId, messages);
        
        // Check if compression needed
        List<Message> allMessages = repository.get(conversationId);
        int totalTokens = tokenCounter.count(allMessages);
        
        if (totalTokens > maxTokens) {
            // Apply compression strategy
            List<Message> compressed = compressionStrategy.compress(allMessages, maxTokens);
            repository.clear(conversationId);
            repository.add(conversationId, compressed);
        }
        
        // Apply retention strategy
        retentionStrategy.cleanup(conversationId, repository);
    }
    
    @Override
    public List<Message> get(String conversationId) {
        List<Message> messages = repository.get(conversationId);
        
        // Enrich with context if needed
        return enrichWithContext(conversationId, messages);
    }
    
    private List<Message> enrichWithContext(String conversationId, List<Message> messages) {
        // Add conversation metadata
        ConversationMetadata metadata = getConversationMetadata(conversationId);
        
        if (metadata != null) {
            SystemMessage contextMessage = new SystemMessage(
                String.format("""
                    Thông tin cuộc trò chuyện:
                    - Bắt đầu: %s
                    - Chủ đề chính: %s
                    - Sở thích người dùng: %s
                    - Ngữ cảnh: %s
                    """, 
                    metadata.getStartTime(),
                    metadata.getMainTopic(),
                    metadata.getUserPreferences(),
                    metadata.getContext()
                ));
            
            List<Message> enriched = new ArrayList<>();
            enriched.add(contextMessage);
            enriched.addAll(messages);
            return enriched;
        }
        
        return messages;
    }
}
```

### 2.2 Semantic Compression Strategy

```java
@Component
public class SemanticCompressionStrategy implements MessageCompressionStrategy {
    
    private final ChatClient compressionClient;
    
    public SemanticCompressionStrategy(ChatModel chatModel) {
        this.compressionClient = ChatClient.builder(chatModel)
            .defaultSystem("""
                Bạn là chuyên gia nén thông tin cuộc trò chuyện.
                Nhiệm vụ: Tóm tắt các tin nhắn thành những điểm quan trọng nhất.
                
                Quy tắc:
                1. Giữ lại thông tin quan trọng về booking khách sạn
                2. Bảo tồn context về sở thích người dùng
                3. Loại bỏ thông tin lặp lại hoặc không cần thiết
                4. Duy trì tone và style của cuộc trò chuyện
                
                Format: Trả về JSON array với structure:
                [
                    {"role": "user", "content": "summary"},
                    {"role": "assistant", "content": "summary"}
                ]
                """)
            .build();
    }
    
    @Override
    public List<Message> compress(List<Message> messages, int targetTokens) {
        if (messages.size() <= 10) {
            return messages; // Too few to compress
        }
        
        // Group messages by conversation segments
        List<List<Message>> segments = segmentMessages(messages);
        List<Message> compressed = new ArrayList<>();
        
        // Always keep system messages and recent messages
        compressed.addAll(getSystemMessages(messages));
        compressed.addAll(getRecentMessages(messages, 4)); // Keep last 4 messages
        
        // Compress older segments
        for (List<Message> segment : segments.subList(0, segments.size() - 2)) {
            Message compressedSegment = compressSegment(segment);
            if (compressedSegment != null) {
                compressed.add(compressedSegment);
            }
        }
        
        return compressed;
    }
    
    private Message compressSegment(List<Message> segment) {
        StringBuilder conversationText = new StringBuilder();
        for (Message msg : segment) {
            conversationText.append(msg.getMessageType().name())
                          .append(": ")
                          .append(msg.getContent())
                          .append("\n");
        }
        
        String compressed = compressionClient.prompt()
            .user("Tóm tắt đoạn hội thoại này:\n\n" + conversationText.toString())
            .call()
            .content();
            
        return new SystemMessage("Tóm tắt cuộc trò chuyện trước: " + compressed);
    }
}
```

### 2.3 Importance-Based Retention

```java
@Component
public class ImportanceBasedRetentionStrategy implements MessageRetentionStrategy {
    
    private final ChatClient importanceEvaluator;
    private final ConversationAnalyzer analyzer;
    
    @Override
    public void cleanup(String conversationId, ChatMemoryRepository repository) {
        List<Message> messages = repository.get(conversationId);
        
        if (messages.size() <= 20) {
            return; // Don't cleanup small conversations
        }
        
        // Analyze message importance
        Map<Message, Double> importanceScores = analyzeImportance(messages);
        
        // Keep high-importance messages and recent messages
        List<Message> toKeep = messages.stream()
            .filter(msg -> {
                double importance = importanceScores.getOrDefault(msg, 0.5);
                boolean isRecent = isRecentMessage(msg, messages);
                boolean isSystemMessage = msg instanceof SystemMessage;
                
                return isSystemMessage || isRecent || importance > 0.7;
            })
            .collect(Collectors.toList());
        
        if (toKeep.size() < messages.size()) {
            repository.clear(conversationId);
            repository.add(conversationId, toKeep);
            
            log.info("Cleaned up conversation {}: {} -> {} messages", 
                   conversationId, messages.size(), toKeep.size());
        }
    }
    
    private Map<Message, Double> analyzeImportance(List<Message> messages) {
        Map<Message, Double> scores = new HashMap<>();
        
        for (Message message : messages) {
            double score = calculateImportanceScore(message);
            scores.put(message, score);
        }
        
        return scores;
    }
    
    private double calculateImportanceScore(Message message) {
        double score = 0.0;
        String content = message.getContent().toLowerCase();
        
        // Booking-related keywords boost importance
        if (content.contains("booking") || content.contains("đặt phòng") || 
            content.contains("reservation") || content.contains("hotel")) {
            score += 0.4;
        }
        
        // User preferences boost importance
        if (content.contains("like") || content.contains("prefer") || 
            content.contains("thích") || content.contains("muốn")) {
            score += 0.3;
        }
        
        // Error or important information
        if (content.contains("error") || content.contains("important") || 
            content.contains("lỗi") || content.contains("quan trọng")) {
            score += 0.5;
        }
        
        // Numbers (prices, dates) are important
        if (content.matches(".*\\d+.*")) {
            score += 0.2;
        }
        
        return Math.min(score, 1.0);
    }
}
```

## 3. Multi-Modal Memory Storage

### 3.1 Hybrid Memory System

```java
@Component
public class HybridMemoryManager {
    
    private final MessageWindowChatMemory shortTermMemory;
    private final VectorStoreChatMemoryAdvisor longTermMemory;
    private final JdbcChatMemoryRepository persistentStorage;
    private final ConversationIndexService indexService;
    
    public HybridMemoryManager(ChatMemoryRepository repository, 
                              VectorStore vectorStore,
                              ChatModel chatModel) {
        
        this.shortTermMemory = MessageWindowChatMemory.builder()
            .chatMemoryRepository(repository)
            .maxMessages(20) // Recent conversation
            .build();
            
        this.longTermMemory = VectorStoreChatMemoryAdvisor.builder()
            .vectorStore(vectorStore)
            .chatClient(ChatClient.builder(chatModel).build())
            .searchRequest(SearchRequest.builder()
                .topK(3)
                .similarityThreshold(0.8)
                .build())
            .build();
            
        this.persistentStorage = (JdbcChatMemoryRepository) repository;
    }
    
    public void storeConversationTurn(String conversationId, 
                                    UserMessage userMessage, 
                                    AssistantMessage assistantMessage) {
        
        // 1. Store in short-term memory
        shortTermMemory.add(conversationId, List.of(userMessage, assistantMessage));
        
        // 2. Extract important information for long-term storage
        ConversationSummary summary = extractImportantInfo(userMessage, assistantMessage);
        if (summary.getImportanceScore() > 0.7) {
            
            // Store in vector database for semantic retrieval
            Document memoryDocument = Document.builder()
                .text(summary.getSummaryText())
                .metadata(Map.of(
                    "conversation_id", conversationId,
                    "timestamp", Instant.now().toString(),
                    "type", "conversation_memory",
                    "importance", summary.getImportanceScore(),
                    "topics", summary.getTopics()
                ))
                .build();
                
            vectorStore.write(List.of(memoryDocument));
        }
        
        // 3. Update conversation index
        indexService.updateConversationIndex(conversationId, summary);
    }
    
    public List<Message> retrieveRelevantMemory(String conversationId, String currentQuery) {
        List<Message> memory = new ArrayList<>();
        
        // 1. Get short-term memory (recent conversation)
        memory.addAll(shortTermMemory.get(conversationId));
        
        // 2. Search long-term memory for relevant past conversations
        List<Document> relevantMemories = vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(currentQuery)
                .topK(3)
                .similarityThreshold(0.7)
                .filterExpression(new FilterExpressionBuilder()
                    .eq("conversation_id", conversationId)
                    .build())
                .build()
        );
        
        // 3. Convert relevant memories to context messages
        for (Document doc : relevantMemories) {
            SystemMessage contextMsg = new SystemMessage(
                "Thông tin từ cuộc trò chuyện trước: " + doc.getText()
            );
            memory.add(0, contextMsg); // Add at beginning
        }
        
        return memory;
    }
}
```

### 3.2 Conversation Analytics

```java
@Component
public class ConversationAnalyzer {
    
    private final ChatClient analyzerClient;
    private final TopicExtractor topicExtractor;
    
    public ConversationAnalyzer(ChatModel chatModel) {
        this.analyzerClient = ChatClient.builder(chatModel)
            .defaultSystem("""
                Bạn là chuyên gia phân tích cuộc trò chuyện khách hàng trong hệ thống đặt phòng khách sạn.
                Phân tích và trích xuất:
                1. Chủ đề chính (main topics)
                2. Sở thích khách hàng (user preferences)  
                3. Mức độ quan trọng (importance level 1-10)
                4. Sentiment của khách hàng
                5. Intent/mục đích của cuộc trò chuyện
                
                Trả về JSON format:
                {
                    "mainTopics": ["topic1", "topic2"],
                    "userPreferences": ["pref1", "pref2"],
                    "importanceScore": 8,
                    "sentiment": "positive/negative/neutral",
                    "intent": "booking/inquiry/complaint/support",
                    "keyInformation": ["key1", "key2"]
                }
                """)
            .build();
    }
    
    public ConversationSummary analyzeConversation(UserMessage userMsg, AssistantMessage assistantMsg) {
        String conversationText = String.format("""
            User: %s
            Assistant: %s
            """, userMsg.getContent(), assistantMsg.getContent());
            
        String analysisResult = analyzerClient.prompt()
            .user("Phân tích đoạn hội thoại này:\n\n" + conversationText)
            .call()
            .content();
            
        return parseAnalysisResult(analysisResult);
    }
    
    public UserProfile extractUserProfile(String conversationId, List<Message> messages) {
        StringBuilder conversationHistory = new StringBuilder();
        for (Message msg : messages) {
            if (msg instanceof UserMessage) {
                conversationHistory.append("User: ").append(msg.getContent()).append("\n");
            }
        }
        
        String profileAnalysis = analyzerClient.prompt()
            .user(String.format("""
                Từ lịch sử trò chuyện này, hãy tạo profile của khách hàng:
                
                %s
                
                Trả về JSON:
                {
                    "preferences": {
                        "hotelType": [],
                        "priceRange": "",
                        "amenities": [],
                        "location": []
                    },
                    "travelStyle": "",
                    "budget": "",
                    "frequency": "",
                    "specialRequests": []
                }
                """, conversationHistory.toString()))
            .call()
            .content();
            
        return parseUserProfile(profileAnalysis);
    }
}
```

## 4. Memory Performance Optimization

### 4.1 Caching Layer

```java
@Component
public class MemoryCacheManager {
    
    private final LoadingCache<String, List<Message>> memoryCache;
    private final LoadingCache<String, ConversationSummary> summaryCache;
    private final ChatMemoryRepository repository;
    
    public MemoryCacheManager(ChatMemoryRepository repository) {
        this.repository = repository;
        
        this.memoryCache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(Duration.ofHours(2))
            .expireAfterAccess(Duration.ofMinutes(30))
            .removalListener(this::onMemoryEviction)
            .build(this::loadMemoryFromRepository);
            
        this.summaryCache = Caffeine.newBuilder()
            .maximumSize(5000)
            .expireAfterWrite(Duration.ofHours(6))
            .build(this::loadSummaryFromRepository);
    }
    
    public List<Message> getCachedMemory(String conversationId) {
        return memoryCache.getUnchecked(conversationId);
    }
    
    public void updateCache(String conversationId, List<Message> messages) {
        memoryCache.put(conversationId, new ArrayList<>(messages));
        
        // Asynchronously update repository
        CompletableFuture.runAsync(() -> {
            try {
                repository.clear(conversationId);
                repository.add(conversationId, messages);
            } catch (Exception e) {
                log.error("Error updating memory repository for conversation {}", conversationId, e);
            }
        });
    }
    
    private List<Message> loadMemoryFromRepository(String conversationId) {
        return repository.get(conversationId);
    }
    
    private void onMemoryEviction(String conversationId, List<Message> messages, 
                                 RemovalCause cause) {
        if (cause == RemovalCause.EXPIRED || cause == RemovalCause.SIZE) {
            // Ensure data is persisted before eviction
            CompletableFuture.runAsync(() -> {
                repository.clear(conversationId);
                repository.add(conversationId, messages);
            });
        }
    }
}
```

### 4.2 Memory Compression và Archiving

```java
@Service
public class MemoryArchiveService {
    
    private final ChatMemoryRepository activeRepository;
    private final ChatMemoryRepository archiveRepository;
    private final CompressionService compressionService;
    
    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
    public void archiveOldConversations() {
        LocalDateTime cutoff = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        
        List<String> oldConversations = findOldConversations(cutoff);
        
        for (String conversationId : oldConversations) {
            try {
                archiveConversation(conversationId);
            } catch (Exception e) {
                log.error("Error archiving conversation {}", conversationId, e);
            }
        }
        
        log.info("Archived {} old conversations", oldConversations.size());
    }
    
    private void archiveConversation(String conversationId) {
        // 1. Get full conversation
        List<Message> messages = activeRepository.get(conversationId);
        
        // 2. Compress conversation
        ConversationArchive archive = compressionService.compress(conversationId, messages);
        
        // 3. Store in archive
        archiveRepository.add(conversationId + "_archive", archive.getCompressedMessages());
        
        // 4. Create summary document for vector search
        Document summaryDoc = Document.builder()
            .text(archive.getSummary())
            .metadata(Map.of(
                "original_conversation_id", conversationId,
                "archive_date", Instant.now().toString(),
                "message_count", messages.size(),
                "compression_ratio", archive.getCompressionRatio()
            ))
            .build();
            
        vectorStore.write(List.of(summaryDoc));
        
        // 5. Remove from active storage
        activeRepository.clear(conversationId);
    }
    
    public List<Message> retrieveArchivedConversation(String conversationId) {
        List<Message> archived = archiveRepository.get(conversationId + "_archive");
        return compressionService.decompress(archived);
    }
}
```

## 5. Context-Aware Memory Advisors

### 5.1 Smart Memory Advisor

```java
@Component
public class SmartMemoryAdvisor implements CallAroundAdvisor {
    
    private final HybridMemoryManager memoryManager;
    private final ConversationAnalyzer analyzer;
    
    @Override
    public String getName() {
        return "SmartMemoryAdvisor";
    }
    
    @Override
    public int getOrder() {
        return -200; // Run early to set context
    }
    
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        String conversationId = (String) advisedRequest.adviseContext()
            .get(ChatMemory.CONVERSATION_ID);
            
        if (conversationId != null) {
            // Retrieve relevant memory based on current query
            List<Message> relevantMemory = memoryManager.retrieveRelevantMemory(
                conversationId, advisedRequest.userText());
            
            // Enhance request with memory context
            String enhancedPrompt = enhancePromptWithMemory(
                advisedRequest.userText(), relevantMemory);
                
            AdvisedRequest enhancedRequest = AdvisedRequest.from(advisedRequest)
                .userText(enhancedPrompt)
                .updateContext(context -> {
                    context.put("retrieved_memory_count", relevantMemory.size());
                    context.put("memory_topics", extractTopics(relevantMemory));
                    return context;
                })
                .build();
                
            AdvisedResponse response = chain.nextAroundCall(enhancedRequest);
            
            // Store conversation turn
            UserMessage userMsg = new UserMessage(advisedRequest.userText());
            AssistantMessage assistantMsg = new AssistantMessage(
                response.chatResponse().getResult().getOutput().getText());
                
            memoryManager.storeConversationTurn(conversationId, userMsg, assistantMsg);
            
            return response;
        }
        
        return chain.nextAroundCall(advisedRequest);
    }
    
    private String enhancePromptWithMemory(String originalPrompt, List<Message> memory) {
        if (memory.isEmpty()) {
            return originalPrompt;
        }
        
        StringBuilder enhanced = new StringBuilder();
        enhanced.append("Thông tin từ cuộc trò chuyện trước:\n");
        
        for (Message msg : memory) {
            if (msg instanceof SystemMessage && msg.getContent().startsWith("Thông tin")) {
                enhanced.append("- ").append(msg.getContent()).append("\n");
            }
        }
        
        enhanced.append("\nCâu hỏi hiện tại: ").append(originalPrompt);
        enhanced.append("\n\nHãy sử dụng thông tin từ cuộc trò chuyện trước để đưa ra câu trả lời phù hợp và nhất quán.");
        
        return enhanced.toString();
    }
}
```

## 6. Memory Configuration

### 6.1 Application Properties

```yaml
spring:
  ai:
    memory:
      # Repository Configuration
      repository:
        type: jdbc # jdbc, cassandra, neo4j, in-memory
        jdbc:
          initialize-schema: embedded
          platform: postgresql
          
      # Memory Strategies
      strategies:
        short-term:
          type: message-window
          max-messages: 20
          
        long-term:
          type: vector-store
          similarity-threshold: 0.7
          max-results: 3
          
        compression:
          enabled: true
          trigger-token-count: 4000
          target-token-count: 2000
          strategy: semantic
          
        retention:
          enabled: true
          strategy: importance-based
          cleanup-interval: 6h
          
      # Performance
      caching:
        enabled: true
        max-size: 10000
        expire-after-write: 2h
        expire-after-access: 30m
        
      # Archiving
      archiving:
        enabled: true
        archive-after: 7d
        compression-enabled: true
        
      # Analytics
      analytics:
        enabled: true
        profile-extraction: true
        sentiment-analysis: true
```

## 7. Testing Framework

### 7.1 Memory System Testing

```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.ai.memory.repository.type=in-memory",
    "spring.ai.memory.caching.enabled=false"
})
class AdvancedMemorySystemTest {
    
    @Autowired
    private HybridMemoryManager memoryManager;
    
    @Autowired
    private ConversationAnalyzer analyzer;
    
    @Test
    void should_store_and_retrieve_conversation_context() {
        // Given
        String conversationId = "test-conv-1";
        UserMessage userMsg = new UserMessage("Tôi muốn tìm khách sạn 5 sao ở Da Nang");
        AssistantMessage assistantMsg = new AssistantMessage("Tôi sẽ giúp bạn tìm khách sạn 5 sao tốt nhất ở Đà Nẵng");
        
        // When
        memoryManager.storeConversationTurn(conversationId, userMsg, assistantMsg);
        
        // Then
        List<Message> memory = memoryManager.retrieveRelevantMemory(conversationId, "Giá phòng như thế nào?");
        assertThat(memory).isNotEmpty();
        assertThat(memory.get(0).getContent()).contains("khách sạn 5 sao");
    }
    
    @Test
    void should_compress_long_conversations() {
        // Given
        String conversationId = "long-conv";
        List<Message> longConversation = createLongConversation(50); // 50 messages
        
        AdaptiveChatMemory memory = new AdaptiveChatMemory(repository, compressionStrategy, retentionStrategy, 1000);
        
        // When
        memory.add(conversationId, longConversation);
        
        // Then
        List<Message> retrieved = memory.get(conversationId);
        assertThat(retrieved.size()).isLessThan(longConversation.size());
        assertThat(retrieved).anyMatch(msg -> msg instanceof SystemMessage && 
                                     msg.getContent().contains("Tóm tắt cuộc trò chuyện"));
    }
    
    @Test
    void should_extract_user_preferences() {
        // Given
        List<Message> conversation = Arrays.asList(
            new UserMessage("Tôi thích khách sạn có hồ bơi và spa"),
            new AssistantMessage("Được, tôi sẽ tìm khách sạn có hồ bơi và spa cho bạn"),
            new UserMessage("Ngân sách của tôi khoảng 2 triệu đồng mỗi đêm"),
            new AssistantMessage("Tôi hiểu, bạn muốn tìm trong tầm giá 2 triệu đồng")
        );
        
        // When
        UserProfile profile = analyzer.extractUserProfile("test-conv", conversation);
        
        // Then
        assertThat(profile.getPreferences().getAmenities()).contains("hồ bơi", "spa");
        assertThat(profile.getBudget()).contains("2 triệu");
    }
}
```

## 8. Migration Plan

### Phase 1: Infrastructure (Week 1-2)
- [ ] Setup JDBC repository
- [ ] Implement basic caching
- [ ] Create memory strategies

### Phase 2: Advanced Features (Week 3-4)
- [ ] Implement hybrid memory system
- [ ] Add conversation analytics
- [ ] Create compression strategies

### Phase 3: Integration (Week 5-6)
- [ ] Integrate with advisor system
- [ ] Add performance monitoring
- [ ] Implement archiving

### Phase 4: Optimization (Week 7-8)
- [ ] Performance tuning
- [ ] Advanced testing
- [ ] Documentation

## 9. Expected Benefits

1. **Scalability**: Support thousands of concurrent conversations
2. **Intelligence**: Context-aware responses based on conversation history
3. **Efficiency**: Smart compression và caching reduce storage costs
4. **Personalization**: User profile extraction for better recommendations
5. **Performance**: Sub-100ms memory retrieval
6. **Reliability**: Persistent storage with backup và recovery

Hệ thống memory mới sẽ làm cho AI trở nên thông minh và cá nhân hóa hơn đáng kể.
