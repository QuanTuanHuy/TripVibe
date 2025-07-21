package huy.project.ai_service.core.service.memory;

import java.util.Map;

/**
 * Interface for conversation memory management
 * Similar to LangChain's ConversationMemory
 */
public interface ConversationMemory {
    
    /**
     * Save a conversation turn (input/output pair)
     */
    void saveContext(String conversationId, String input, String output);
    
    /**
     * Get formatted memory buffer for use in prompts
     */
    String getMemoryBuffer(String conversationId);
    
    /**
     * Clear conversation history
     */
    void clearHistory(String conversationId);
    
    /**
     * Get memory variables as key-value pairs
     */
    Map<String, Object> getMemoryVariables(String conversationId);
    
    /**
     * Get memory statistics
     */
    MemoryStats getMemoryStats(String conversationId);
    
    /**
     * Clean up expired conversations
     */
    void cleanup();
}
