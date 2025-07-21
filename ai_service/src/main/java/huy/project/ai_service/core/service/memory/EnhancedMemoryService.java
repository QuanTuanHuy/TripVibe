package huy.project.ai_service.core.service.memory;

import huy.project.ai_service.core.domain.constant.MemoryType;
import huy.project.ai_service.core.service.memory.impl.EnhancedBufferMemory;
import huy.project.ai_service.core.service.memory.impl.SummaryMemory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Enhanced memory service that provides LangChain-like memory capabilities
 * for Spring AI applications
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnhancedMemoryService {
    
    private final EnhancedBufferMemory bufferMemory;
    private final SummaryMemory summaryMemory;
    
    @Value("${spring.ai.memory.type:ENHANCED_BUFFER}")
    private String memoryTypeString;
    
    @Value("${spring.ai.memory.cleanup.enabled:true}")
    private boolean cleanupEnabled;
    
    /**
     * Save conversation context using the configured memory strategy
     */
    public void saveContext(String conversationId, String input, String output) {
        try {
            getActiveMemory().saveContext(conversationId, input, output);
        } catch (Exception e) {
            log.error("Error saving conversation context for {}: {}", conversationId, e.getMessage());
            // Fallback to buffer memory
            if (!isBufferMemory()) {
                bufferMemory.saveContext(conversationId, input, output);
            }
        }
    }
    
    /**
     * Get formatted memory buffer for use in prompts
     */
    public String getMemoryBuffer(String conversationId) {
        try {
            return getActiveMemory().getMemoryBuffer(conversationId);
        } catch (Exception e) {
            log.error("Error getting memory buffer for {}: {}", conversationId, e.getMessage());
            // Fallback to buffer memory
            if (!isBufferMemory()) {
                return bufferMemory.getMemoryBuffer(conversationId);
            }
            return "Chưa có lịch sử trò chuyện.";
        }
    }
    
    /**
     * Get memory variables for prompt templates
     */
    public Map<String, Object> getMemoryVariables(String conversationId) {
        try {
            return getActiveMemory().getMemoryVariables(conversationId);
        } catch (Exception e) {
            log.error("Error getting memory variables for {}: {}", conversationId, e.getMessage());
            return Map.of("history", "Chưa có lịch sử trò chuyện.");
        }
    }
    
    /**
     * Get memory statistics
     */
    public MemoryStats getMemoryStats(String conversationId) {
        try {
            return getActiveMemory().getMemoryStats(conversationId);
        } catch (Exception e) {
            log.error("Error getting memory stats for {}: {}", conversationId, e.getMessage());
            return MemoryStats.builder()
                    .totalTurns(0)
                    .totalTokens(0)
                    .memoryType(memoryTypeString)
                    .build();
        }
    }
    
    /**
     * Clear conversation history
     */
    public void clearHistory(String conversationId) {
        try {
            getActiveMemory().clearHistory(conversationId);
            log.info("Cleared history for conversation: {}", conversationId);
        } catch (Exception e) {
            log.error("Error clearing history for {}: {}", conversationId, e.getMessage());
        }
    }
    
    /**
     * Get the currently active memory strategy
     */
    private ConversationMemory getActiveMemory() {
        MemoryType memoryType = parseMemoryType();
        return switch (memoryType) {
            case SUMMARY -> summaryMemory;
            case BUFFER -> bufferMemory;
            default -> {
                log.warn("Unsupported memory type: {}, falling back to BUFFER", memoryType);
                yield bufferMemory;
            }
        };
    }
    
    private MemoryType parseMemoryType() {
        try {
            // Handle legacy naming
            if ("ENHANCED_BUFFER".equals(memoryTypeString)) {
                return MemoryType.BUFFER;
            }
            return MemoryType.valueOf(memoryTypeString);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid memory type: {}, using BUFFER as default", memoryTypeString);
            return MemoryType.BUFFER;
        }
    }
    
    private boolean isBufferMemory() {
        MemoryType type = parseMemoryType();
        return type == MemoryType.BUFFER;
    }
    
    /**
     * Scheduled cleanup of expired conversations
     * Runs every hour by default
     */
    @Scheduled(fixedRateString = "${spring.ai.memory.cleanup.interval:3600000}") // 1 hour
    public void scheduledCleanup() {
        if (!cleanupEnabled) {
            return;
        }
        
        try {
            log.debug("Starting scheduled memory cleanup");
            bufferMemory.cleanup();
            summaryMemory.cleanup();
            log.debug("Completed scheduled memory cleanup");
        } catch (Exception e) {
            log.error("Error during scheduled memory cleanup: {}", e.getMessage());
        }
    }
    
    /**
     * Manual cleanup trigger
     */
    public void manualCleanup() {
        try {
            log.info("Starting manual memory cleanup");
            bufferMemory.cleanup();
            summaryMemory.cleanup();
            log.info("Completed manual memory cleanup");
        } catch (Exception e) {
            log.error("Error during manual memory cleanup: {}", e.getMessage());
        }
    }
    
    /**
     * Get system memory statistics
     */
    public Map<String, Object> getSystemMemoryStats() {
        return Map.of(
                "active_memory_type", memoryTypeString,
                "cleanup_enabled", cleanupEnabled,
                "buffer_conversations", bufferMemory.getClass().getSimpleName(),
                "summary_conversations", summaryMemory.getClass().getSimpleName()
        );
    }
}
