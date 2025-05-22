package huy.project.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/authentication")
    public ResponseEntity<Map<String, Object>> authenticationServiceFallback() {
        Map<String, Object> response = createFallbackResponse(
                "Authentication service is temporarily unavailable",
                "AUTH_SERVICE_DOWN"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/accommodation")
    public ResponseEntity<Map<String, Object>> accommodationServiceFallback() {
        Map<String, Object> response = createFallbackResponse(
                "Accommodation service is temporarily unavailable",
                "ACCOMMODATION_SERVICE_DOWN"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchServiceFallback() {
        Map<String, Object> response = createFallbackResponse(
                "Search service is temporarily unavailable",
                "SEARCH_SERVICE_DOWN"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/booking")
    public ResponseEntity<Map<String, Object>> bookingServiceFallback() {
        Map<String, Object> response = createFallbackResponse(
                "Booking service is temporarily unavailable",
                "BOOKING_SERVICE_DOWN"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/payment")
    public ResponseEntity<Map<String, Object>> paymentServiceFallback() {
        Map<String, Object> response = createFallbackResponse(
                "Payment service is temporarily unavailable",
                "PAYMENT_SERVICE_DOWN"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> profileServiceFallback() {
        Map<String, Object> response = createFallbackResponse(
                "Profile service is temporarily unavailable",
                "PROFILE_SERVICE_DOWN"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/notification")
    public ResponseEntity<Map<String, Object>> notificationServiceFallback() {
        Map<String, Object> response = createFallbackResponse(
                "Notification service is temporarily unavailable",
                "NOTIFICATION_SERVICE_DOWN"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/rating")
    public ResponseEntity<Map<String, Object>> ratingServiceFallback() {
        Map<String, Object> response = createFallbackResponse(
                "Rating service is temporarily unavailable",
                "RATING_SERVICE_DOWN"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/promotion")
    public ResponseEntity<Map<String, Object>> promotionServiceFallback() {
        Map<String, Object> response = createFallbackResponse(
                "Promotion service is temporarily unavailable",
                "PROMOTION_SERVICE_DOWN"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/location")
    public ResponseEntity<Map<String, Object>> locationServiceFallback() {
        Map<String, Object> response = createFallbackResponse(
                "Location service is temporarily unavailable",
                "LOCATION_SERVICE_DOWN"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/file")
    public ResponseEntity<Map<String, Object>> fileServiceFallback() {
        Map<String, Object> response = createFallbackResponse(
                "File service is temporarily unavailable",
                "FILE_SERVICE_DOWN"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/chat")
    public ResponseEntity<Map<String, Object>> chatServiceFallback() {
        Map<String, Object> response = createFallbackResponse(
                "Chat service is temporarily unavailable",
                "CHAT_SERVICE_DOWN"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    private Map<String, Object> createFallbackResponse(String message, String errorCode) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("errorCode", errorCode);
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        return response;
    }
}
