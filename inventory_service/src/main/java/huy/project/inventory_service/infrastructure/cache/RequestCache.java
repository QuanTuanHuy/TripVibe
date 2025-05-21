package huy.project.inventory_service.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * Request cache to support idempotent operations
 * Similar to how Booking.com handles API idempotency
 */
@Component
@Slf4j
public class RequestCache {

    private final Cache<String, Object> responseCache;
    private final Cache<String, Object> inProgressCache;

    public RequestCache() {
        // Cache for completed responses
        this.responseCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(30))
                .recordStats()
                .build();

        // Cache for in-progress operations to prevent duplicate processing
        this.inProgressCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
    }

    /**
     * Execute operation with idempotency
     * 
     * @param requestId The unique request identifier
     * @param supplier The operation to execute if not already processed
     * @return The result of the operation
     */
    public <T> T executeIdempotent(String requestId, Supplier<T> supplier) {
        Objects.requireNonNull(requestId, "Request ID must not be null");
        
        // Check if we already have a result for this request
        @SuppressWarnings("unchecked")
        T cachedResult = (T) responseCache.getIfPresent(requestId);
        if (cachedResult != null) {
            log.debug("Using cached result for request: {}", requestId);
            return cachedResult;
        }
        
        // Check if this request is already in progress
        if (isRequestInProgress(requestId)) {
            log.warn("Duplicate request detected: {}", requestId);
            throw new IllegalStateException("Request already in progress: " + requestId);
        }
        
        try {
            // Mark this request as in progress
            markRequestInProgress(requestId);
            
            // Execute the operation
            T result = supplier.get();
            
            // Cache the result
            responseCache.put(requestId, result);
            
            return result;
        } finally {
            // Clear the in-progress mark
            clearInProgressMark(requestId);
        }
    }
    
    /**
     * Get all cached responses for debugging
     */
    public ConcurrentMap<String, Object> getAllCachedResponses() {
        return responseCache.asMap();
    }
    
    /**
     * Clear cached response for a request
     */
    public void clearCachedResponse(String requestId) {
        responseCache.invalidate(requestId);
    }
    
    /**
     * Check if request is already being processed
     */
    private boolean isRequestInProgress(String requestId) {
        return inProgressCache.getIfPresent(requestId) != null;
    }
    
    /**
     * Mark request as in progress
     */
    private void markRequestInProgress(String requestId) {
        inProgressCache.put(requestId, Boolean.TRUE);
    }
    
    /**
     * Clear in-progress mark
     */
    private void clearInProgressMark(String requestId) {
        inProgressCache.invalidate(requestId);
    }
}
