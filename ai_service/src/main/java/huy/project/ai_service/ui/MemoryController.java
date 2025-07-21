package huy.project.ai_service.ui;

import huy.project.ai_service.core.service.memory.EnhancedMemoryService;
import huy.project.ai_service.core.service.memory.MemoryStats;
import huy.project.ai_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API endpoints for memory management and monitoring
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/v1/memory")
public class MemoryController {
    
    private final EnhancedMemoryService memoryService;
    
    /**
     * Get memory statistics for a specific conversation
     */
    @GetMapping("/stats/{conversationId}")
    public ResponseEntity<Resource<MemoryStats>> getMemoryStats(@PathVariable String conversationId) {
        MemoryStats stats = memoryService.getMemoryStats(conversationId);
        return ResponseEntity.ok(new Resource<>(stats));
    }
    
    /**
     * Get memory variables for a conversation (useful for debugging)
     */
    @GetMapping("/variables/{conversationId}")
    public ResponseEntity<Resource<Map<String, Object>>> getMemoryVariables(@PathVariable String conversationId) {
        Map<String, Object> variables = memoryService.getMemoryVariables(conversationId);
        return ResponseEntity.ok(new Resource<>(variables));
    }
    
    /**
     * Get system-wide memory statistics
     */
    @GetMapping("/system/stats")
    public ResponseEntity<Resource<Map<String, Object>>> getSystemStats() {
        Map<String, Object> stats = memoryService.getSystemMemoryStats();
        return ResponseEntity.ok(new Resource<>(stats));
    }
    
    /**
     * Clear memory for a specific conversation
     */
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Resource<String>> clearConversationMemory(@PathVariable String conversationId) {
        memoryService.clearHistory(conversationId);
        return ResponseEntity.ok(new Resource<>("Memory cleared for conversation: " + conversationId));
    }
    
    /**
     * Trigger manual cleanup of expired conversations
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Resource<String>> triggerCleanup() {
        memoryService.manualCleanup();
        return ResponseEntity.ok(new Resource<>("Memory cleanup completed"));
    }
    
    /**
     * Get formatted memory buffer for a conversation (for debugging)
     */
    @GetMapping("/buffer/{conversationId}")
    public ResponseEntity<Resource<String>> getMemoryBuffer(@PathVariable String conversationId) {
        String buffer = memoryService.getMemoryBuffer(conversationId);
        return ResponseEntity.ok(new Resource<>(buffer));
    }
}
