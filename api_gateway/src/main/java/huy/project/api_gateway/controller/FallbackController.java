package huy.project.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/{service}")
    @PostMapping("/{service}")
    public ResponseEntity<Map<String, Object>> serviceFallback(@PathVariable String service) {
        Map<String, Object> response = Map.of(
                "meta", Map.of(
                        "code", 503001,
                        "message", service.substring(0, 1).toUpperCase() + service.substring(1) + " service is temporarily unavailable. Please try again later."
                ),
                "data", null
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    // Specific fallback methods for each service
    @GetMapping("/authentication")
    @PostMapping("/authentication")
    public ResponseEntity<Map<String, Object>> authenticationFallback() {
        return serviceFallback("authentication");
    }
    
    @GetMapping("/accommodation")
    @PostMapping("/accommodation")
    public ResponseEntity<Map<String, Object>> accommodationFallback() {
        return serviceFallback("accommodation");
    }
    
    @GetMapping("/search")
    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> searchFallback() {
        return serviceFallback("search");
    }
    
    @GetMapping("/booking")
    @PostMapping("/booking")
    public ResponseEntity<Map<String, Object>> bookingFallback() {
        return serviceFallback("booking");
    }
    
    @GetMapping("/file")
    @PostMapping("/file")
    public ResponseEntity<Map<String, Object>> fileFallback() {
        return serviceFallback("file");
    }
    
    @GetMapping("/profile")
    @PostMapping("/profile")
    public ResponseEntity<Map<String, Object>> profileFallback() {
        return serviceFallback("profile");
    }
    
    @GetMapping("/rating")
    @PostMapping("/rating")
    public ResponseEntity<Map<String, Object>> ratingFallback() {
        return serviceFallback("rating");
    }
    
    @GetMapping("/promotion")
    @PostMapping("/promotion")
    public ResponseEntity<Map<String, Object>> promotionFallback() {
        return serviceFallback("promotion");
    }
    
    @GetMapping("/notification")
    @PostMapping("/notification")
    public ResponseEntity<Map<String, Object>> notificationFallback() {
        return serviceFallback("notification");
    }
    
    @GetMapping("/chat")
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chatFallback() {
        return serviceFallback("chat");
    }
    
    @GetMapping("/location")
    @PostMapping("/location")
    public ResponseEntity<Map<String, Object>> locationFallback() {
        return serviceFallback("location");
    }
    
    @GetMapping("/payment")
    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> paymentFallback() {
        return serviceFallback("payment");
    }
    
    @GetMapping("/inventory")
    @PostMapping("/inventory")
    public ResponseEntity<Map<String, Object>> inventoryFallback() {
        return serviceFallback("inventory");
    }
}
