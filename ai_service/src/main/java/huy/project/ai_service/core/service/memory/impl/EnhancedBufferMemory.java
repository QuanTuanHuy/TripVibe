package huy.project.ai_service.core.service.memory.impl;

import huy.project.ai_service.core.domain.model.ConversationTurn;
import huy.project.ai_service.core.service.memory.ConversationMemory;
import huy.project.ai_service.core.service.memory.MemoryStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced buffer memory with token limits and TTL
 * Similar to LangChain's ConversationBufferWindowMemory
 */
@Component
@Slf4j
public class EnhancedBufferMemory implements ConversationMemory {
    
    private final Map<String, List<ConversationTurn>> conversations = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastAccessed = new ConcurrentHashMap<>();
    
    @Value("${spring.ai.memory.max-tokens:4000}")
    private int maxTokens;
    
    @Value("${spring.ai.memory.max-turns:20}")
    private int maxTurns;
    
    @Value("${spring.ai.memory.ttl-hours:24}")
    private int ttlHours;
    
    @Override
    public void saveContext(String conversationId, String input, String output) {
        ConversationTurn turn = ConversationTurn.builder()
                .id(UUID.randomUUID().toString())
                .conversationId(conversationId)
                .userMessage(input)
                .aiResponse(output)
                .timestamp(LocalDateTime.now())
                .tokenCount(estimateTokens(input) + estimateTokens(output))
                .metadata(new HashMap<>())
                .build();
        
        conversations.computeIfAbsent(conversationId, k -> new ArrayList<>()).add(turn);
        lastAccessed.put(conversationId, LocalDateTime.now());
        
        // Truncate if necessary
        truncateConversation(conversationId);
        
        log.debug("Saved conversation turn for {}, total turns: {}", 
                conversationId, conversations.get(conversationId).size());
    }
    
    @Override
    public String getMemoryBuffer(String conversationId) {
        List<ConversationTurn> turns = conversations.get(conversationId);
        if (turns == null || turns.isEmpty()) {
            return "Chưa có lịch sử trò chuyện.";
        }
        
        lastAccessed.put(conversationId, LocalDateTime.now());
        
        // Get valid turns within token limit
        List<ConversationTurn> validTurns = getValidTurns(turns);
        
        StringBuilder buffer = new StringBuilder();
        for (ConversationTurn turn : validTurns) {
            buffer.append("Người dùng: ").append(turn.getUserMessage()).append("\n");
            buffer.append("AI: ").append(turn.getAiResponse()).append("\n");
        }
        
        return buffer.toString();
    }
    
    @Override
    public void clearHistory(String conversationId) {
        conversations.remove(conversationId);
        lastAccessed.remove(conversationId);
        log.info("Cleared conversation history for {}", conversationId);
    }
    
    @Override
    public Map<String, Object> getMemoryVariables(String conversationId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("history", getMemoryBuffer(conversationId));
        
        List<ConversationTurn> turns = conversations.get(conversationId);
        if (turns != null) {
            variables.put("turn_count", turns.size());
            variables.put("total_tokens", turns.stream().mapToInt(ConversationTurn::getTotalTokens).sum());
        }
        
        return variables;
    }
    
    @Override
    public MemoryStats getMemoryStats(String conversationId) {
        List<ConversationTurn> turns = conversations.get(conversationId);
        if (turns == null || turns.isEmpty()) {
            return MemoryStats.builder()
                    .totalTurns(0)
                    .totalTokens(0)
                    .memoryType("ENHANCED_BUFFER")
                    .build();
        }
        
        int totalTokens = turns.stream().mapToInt(ConversationTurn::getTotalTokens).sum();
        
        return MemoryStats.builder()
                .totalTurns(turns.size())
                .totalTokens(totalTokens)
                .firstTurn(turns.get(0).getTimestamp())
                .lastTurn(turns.get(turns.size() - 1).getTimestamp())
                .averageTokensPerTurn((double) totalTokens / turns.size())
                .memoryType("ENHANCED_BUFFER")
                .isCompressed(false)
                .build();
    }
    
    @Override
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(ttlHours);
        
        conversations.entrySet().removeIf(entry -> {
            String conversationId = entry.getKey();
            LocalDateTime lastAccessTime = lastAccessed.get(conversationId);
            
            if (lastAccessTime != null && lastAccessTime.isBefore(cutoff)) {
                lastAccessed.remove(conversationId);
                log.info("Cleaned up expired conversation: {}", conversationId);
                return true;
            }
            return false;
        });
    }
    
    private List<ConversationTurn> getValidTurns(List<ConversationTurn> turns) {
        List<ConversationTurn> validTurns = new ArrayList<>();
        int totalTokens = 0;
        
        // Start from the most recent and work backwards
        for (int i = turns.size() - 1; i >= 0; i--) {
            ConversationTurn turn = turns.get(i);
            int turnTokens = turn.getTotalTokens();
            
            if (totalTokens + turnTokens > maxTokens || validTurns.size() >= maxTurns) {
                break;
            }
            
            validTurns.add(0, turn); // Add to beginning to maintain order
            totalTokens += turnTokens;
        }
        
        return validTurns;
    }
    
    private void truncateConversation(String conversationId) {
        List<ConversationTurn> turns = conversations.get(conversationId);
        if (turns == null) return;
        
        // Remove old turns if we exceed limits
        while (turns.size() > maxTurns * 2) { // Keep 2x buffer before aggressive cleanup
            turns.remove(0);
        }
        
        // Token-based truncation
        int totalTokens = turns.stream().mapToInt(ConversationTurn::getTotalTokens).sum();
        while (totalTokens > maxTokens * 2 && turns.size() > 1) {
            ConversationTurn removed = turns.remove(0);
            totalTokens -= removed.getTotalTokens();
        }
    }
    
    private int estimateTokens(String text) {
        if (text == null) return 0;
        // Rough estimation: ~4 characters per token for Vietnamese/English
        return (int) Math.ceil(text.length() / 4.0);
    }
}
