package huy.project.ai_service.core.service.memory.impl;

import huy.project.ai_service.core.domain.model.ConversationTurn;
import huy.project.ai_service.core.service.memory.ConversationMemory;
import huy.project.ai_service.core.service.memory.MemoryStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Summary memory that compresses old conversations using LLM
 * Similar to LangChain's ConversationSummaryBufferMemory
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SummaryMemory implements ConversationMemory {
    
    private final ChatClient chatClient;
    
    private final Map<String, String> conversationSummaries = new ConcurrentHashMap<>();
    private final Map<String, List<ConversationTurn>> recentTurns = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastAccessed = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastSummary = new ConcurrentHashMap<>();
    
    @Value("${spring.ai.memory.max-tokens:4000}")
    private int maxTokens;
    
    @Value("${spring.ai.memory.summary-threshold:2000}")
    private int summaryThreshold;
    
    @Value("${spring.ai.memory.ttl-hours:24}")
    private int ttlHours;
    
    private static final String SUMMARY_PROMPT_TEMPLATE = """
            Hãy tóm tắt cuộc trò chuyện sau đây một cách ngắn gọn và chính xác.
            Giữ lại thông tin quan trọng, bối cảnh và các chi tiết cần thiết.
            
            Tóm tắt hiện tại: %s
            
            Thông tin trò chuyện mới:
            %s
            
            Tạo tóm tắt cập nhật (chỉ trả về nội dung tóm tắt, không giải thích):
            """;
    
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
        
        recentTurns.computeIfAbsent(conversationId, k -> new ArrayList<>()).add(turn);
        lastAccessed.put(conversationId, LocalDateTime.now());
        
        // Check if we need to summarize
        if (shouldSummarize(conversationId)) {
            summarizeConversation(conversationId);
        }
        
        log.debug("Saved conversation turn for {}", conversationId);
    }
    
    @Override
    public String getMemoryBuffer(String conversationId) {
        lastAccessed.put(conversationId, LocalDateTime.now());
        
        StringBuilder buffer = new StringBuilder();
        
        // Add summary if exists
        String summary = conversationSummaries.get(conversationId);
        if (summary != null && !summary.isEmpty()) {
            buffer.append("Tóm tắt cuộc trò chuyện trước:\n").append(summary).append("\n\n");
        }
        
        // Add recent turns
        List<ConversationTurn> turns = recentTurns.get(conversationId);
        if (turns != null && !turns.isEmpty()) {
            buffer.append("Lịch sử gần đây:\n");
            List<ConversationTurn> validTurns = getValidRecentTurns(turns);
            
            for (ConversationTurn turn : validTurns) {
                buffer.append("Người dùng: ").append(turn.getUserMessage()).append("\n");
                buffer.append("AI: ").append(turn.getAiResponse()).append("\n");
            }
        }
        
        return buffer.toString().isEmpty() ? "Chưa có lịch sử trò chuyện." : buffer.toString();
    }
    
    @Override
    public void clearHistory(String conversationId) {
        conversationSummaries.remove(conversationId);
        recentTurns.remove(conversationId);
        lastAccessed.remove(conversationId);
        lastSummary.remove(conversationId);
        log.info("Cleared conversation history for {}", conversationId);
    }
    
    @Override
    public Map<String, Object> getMemoryVariables(String conversationId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("history", getMemoryBuffer(conversationId));
        variables.put("summary", conversationSummaries.get(conversationId));
        
        List<ConversationTurn> turns = recentTurns.get(conversationId);
        if (turns != null) {
            variables.put("recent_turn_count", turns.size());
            variables.put("recent_tokens", turns.stream().mapToInt(ConversationTurn::getTotalTokens).sum());
        }
        
        variables.put("has_summary", conversationSummaries.containsKey(conversationId));
        variables.put("last_summary", lastSummary.get(conversationId));
        
        return variables;
    }
    
    @Override
    public MemoryStats getMemoryStats(String conversationId) {
        List<ConversationTurn> turns = recentTurns.get(conversationId);
        String summary = conversationSummaries.get(conversationId);
        
        int totalTurns = turns != null ? turns.size() : 0;
        int totalTokens = turns != null ? 
                turns.stream().mapToInt(ConversationTurn::getTotalTokens).sum() : 0;
        
        // Add summary tokens
        if (summary != null) {
            totalTokens += estimateTokens(summary);
        }
        
        return MemoryStats.builder()
                .totalTurns(totalTurns)
                .totalTokens(totalTokens)
                .firstTurn(turns != null && !turns.isEmpty() ? turns.get(0).getTimestamp() : null)
                .lastTurn(turns != null && !turns.isEmpty() ? 
                         turns.get(turns.size() - 1).getTimestamp() : null)
                .averageTokensPerTurn(totalTurns > 0 ? (double) totalTokens / totalTurns : 0)
                .memoryType("SUMMARY")
                .isCompressed(summary != null)
                .lastCompression(lastSummary.get(conversationId))
                .build();
    }
    
    @Override
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(ttlHours);
        
        // Clean up conversations
        conversationSummaries.entrySet().removeIf(entry -> {
            String conversationId = entry.getKey();
            LocalDateTime lastAccessTime = lastAccessed.get(conversationId);
            
            if (lastAccessTime != null && lastAccessTime.isBefore(cutoff)) {
                recentTurns.remove(conversationId);
                lastAccessed.remove(conversationId);
                lastSummary.remove(conversationId);
                log.info("Cleaned up expired conversation: {}", conversationId);
                return true;
            }
            return false;
        });
    }
    
    private boolean shouldSummarize(String conversationId) {
        List<ConversationTurn> turns = recentTurns.get(conversationId);
        if (turns == null || turns.size() < 5) return false; // Wait for some conversation
        
        int totalTokens = turns.stream().mapToInt(ConversationTurn::getTotalTokens).sum();
        return totalTokens > summaryThreshold;
    }
    
    private void summarizeConversation(String conversationId) {
        try {
            List<ConversationTurn> turns = recentTurns.get(conversationId);
            if (turns == null || turns.isEmpty()) return;
            
            String currentSummary = conversationSummaries.getOrDefault(conversationId, "");
            String recentConversation = formatTurnsForSummary(turns);
            
            String prompt = String.format(SUMMARY_PROMPT_TEMPLATE, currentSummary, recentConversation);
            
            String newSummary = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            
            conversationSummaries.put(conversationId, newSummary);
            lastSummary.put(conversationId, LocalDateTime.now());
            
            // Keep only recent turns (last few)
            if (turns.size() > 3) {
                List<ConversationTurn> recentOnly = new ArrayList<>(
                        turns.subList(Math.max(0, turns.size() - 3), turns.size())
                );
                recentTurns.put(conversationId, recentOnly);
            }
            
            log.info("Summarized conversation {}, new summary length: {} tokens", 
                    conversationId, estimateTokens(newSummary));
            
        } catch (Exception e) {
            log.error("Error summarizing conversation {}: {}", conversationId, e.getMessage());
        }
    }
    
    private String formatTurnsForSummary(List<ConversationTurn> turns) {
        StringBuilder sb = new StringBuilder();
        for (ConversationTurn turn : turns) {
            sb.append("Người dùng: ").append(turn.getUserMessage()).append("\n");
            sb.append("AI: ").append(turn.getAiResponse()).append("\n\n");
        }
        return sb.toString();
    }
    
    private List<ConversationTurn> getValidRecentTurns(List<ConversationTurn> turns) {
        List<ConversationTurn> validTurns = new ArrayList<>();
        int totalTokens = 0;
        int remainingTokens = maxTokens;
        
        // Account for summary tokens
        String summary = conversationSummaries.get(turns.get(0).getConversationId());
        if (summary != null) {
            remainingTokens -= estimateTokens(summary);
        }
        
        // Start from the most recent and work backwards
        for (int i = turns.size() - 1; i >= 0; i--) {
            ConversationTurn turn = turns.get(i);
            int turnTokens = turn.getTotalTokens();
            
            if (totalTokens + turnTokens > remainingTokens) {
                break;
            }
            
            validTurns.add(0, turn); // Add to beginning to maintain order
            totalTokens += turnTokens;
        }
        
        return validTurns;
    }
    
    private int estimateTokens(String text) {
        if (text == null) return 0;
        // Rough estimation: ~4 characters per token for Vietnamese/English
        return (int) Math.ceil(text.length() / 4.0);
    }
}
