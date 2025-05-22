package huy.project.api_gateway.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class StatusController {

    private final HealthEndpoint healthEndpoint;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // Overall application status
        status.put("status", healthEndpoint.health().getStatus().equals(Status.UP) ? "UP" : "DOWN");
        status.put("timestamp", System.currentTimeMillis());
        
        // Circuit breaker statuses
        Map<String, String> circuitBreakerStates = new HashMap<>();
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(cb -> 
            circuitBreakerStates.put(cb.getName(), cb.getState().name())
        );
        status.put("circuitBreakers", circuitBreakerStates);
        
        // Rate limiter statuses
        Map<String, Object> rateLimiterStates = new HashMap<>();
        rateLimiterRegistry.getAllRateLimiters().forEach(rl -> {
            Map<String, Object> rlInfo = new HashMap<>();
            rlInfo.put("availablePermissions", rl.getMetrics().getAvailablePermissions());
            rlInfo.put("numberOfWaitingThreads", rl.getMetrics().getNumberOfWaitingThreads());
            rateLimiterStates.put(rl.getName(), rlInfo);
        });
        status.put("rateLimiters", rateLimiterStates);
        
        return ResponseEntity.ok(status);
    }
}
