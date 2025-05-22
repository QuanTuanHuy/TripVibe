package huy.project.api_gateway.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    // Cache of rate limiters per client IP
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Default rate limit: 50 requests per minute with smooth refill
    private final int DEFAULT_CAPACITY = 50;
    private final int DEFAULT_REFILL_TOKENS = 50;
    private final int DEFAULT_REFILL_MINUTES = 1;

    // Higher limits for specific paths (example: search API gets 100 reqs/minute)
    private final Map<String, Integer> pathLimits = Map.of(
            "/search_service/", 100,
            "/accommodation_service/", 80,
            "/booking_service/", 40,
            "/payment_service/", 20
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip for OPTIONS requests
        if ("OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get client IP (or potentially API key/token in a more sophisticated implementation)
        String clientId = getClientIdentifier(request);

        // Get or create rate limiter bucket for this client
        Bucket bucket = buckets.computeIfAbsent(clientId, this::createNewBucket);

        // Check if request can be processed
        if (bucket.tryConsume(1)) {
            // Add headers to communicate rate limit status
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(bucket.getAvailableTokens()));
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            log.warn("Rate limit exceeded for client: {}", clientId);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
        }
    }

    private String getClientIdentifier(HttpServletRequest request) {
        // In production, this should use a more sophisticated approach, possibly using 
        // authenticated user ID or API key as well
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        // Add the request path to allow different rates for different endpoints
        String requestUri = request.getRequestURI();
        return clientIp + "|" + requestUri;
    }

    private Bucket createNewBucket(String clientId) {
        int capacity = DEFAULT_CAPACITY;

        // Check if request is for a path with custom limits
        String[] parts = clientId.split("\\|");
        String requestUri = parts.length > 1 ? parts[1] : "";

        for (Map.Entry<String, Integer> entry : pathLimits.entrySet()) {
            if (requestUri != null && requestUri.startsWith(entry.getKey())) {
                capacity = entry.getValue();
                break;
            }
        }

        // Create bandwidth with smooth refill (evenly distribute token refill over time)
        Bandwidth limit = Bandwidth.classic(capacity,
                Refill.greedy(DEFAULT_REFILL_TOKENS, Duration.ofMinutes(DEFAULT_REFILL_MINUTES)));

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip rate limiting for actuator endpoints
        return request.getRequestURI().contains("/actuator/");
    }
}
