package huy.project.ai_service.core.domain.constant;

public enum MemoryType {
    /**
     * Simple buffer that stores last N messages
     */
    BUFFER,
    
    /**
     * Sliding window with token limit
     */
    BUFFER_WINDOW,
    
    /**
     * Summarizes old conversations, keeps recent messages
     */
    SUMMARY,
    
    /**
     * Extracts and remembers entities from conversations
     */
    ENTITY,
    
    /**
     * Builds knowledge graph from conversations
     */
    KNOWLEDGE_GRAPH,
    
    /**
     * Vector-based semantic memory
     */
    VECTOR
}
