package huy.project.api_gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        // Generate unique request ID
        String requestId = UUID.randomUUID().toString();
        
        // Wrap request and response to allow multiple reads of the body
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        // Add request ID to MDC for logging context
        MDC.put("requestId", requestId);
        responseWrapper.setHeader("X-Request-ID", requestId);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Pre-processing log
            if (log.isDebugEnabled()) {
                log.debug("Incoming request: {} {} from {}", 
                        request.getMethod(), 
                        request.getRequestURI(),
                        request.getRemoteAddr());
            }
            
            // Pass to the next filter
            filterChain.doFilter(requestWrapper, responseWrapper);
            
            // Post-processing log
            long duration = System.currentTimeMillis() - startTime;
            
            // Only log detailed request/response info for non-200 status
            int status = responseWrapper.getStatus();
            if (status >= 400) {
                log.warn("Request: {} {} completed with status {} in {}ms", 
                        requestWrapper.getMethod(),
                        requestWrapper.getRequestURI(), 
                        status,
                        duration);
            } else {
                log.info("Request: {} {} completed with status {} in {}ms", 
                        requestWrapper.getMethod(),
                        requestWrapper.getRequestURI(), 
                        status,
                        duration);
            }
        } finally {
            // Always copy content of the cached response wrapper to the original response
            responseWrapper.copyBodyToResponse();
            // Clean up MDC
            MDC.remove("requestId");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Don't log actuator health check requests to avoid noise
        return request.getRequestURI().contains("/actuator/health");
    }
}
