package huy.project.payment_service.kernel.property;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.payment")
@Getter
@Setter
public class PaymentProperties {

    private String defaultGateway;
    private Map<String, GatewayProperties> gateways = new HashMap<>();

    @Data
    public static class GatewayProperties {
        private Map<String, String> properties = new HashMap<>();

        // Common properties
        private String successUrl;
        private String cancelUrl;

        // Provider-specific fields
        private String apiKey;  // Stripe
        private String webhookSecret;  // Stripe
        private String clientId;  // PayPal
        private String clientSecret;  // PayPal
        private String terminalId;  // VNPay
        private String secretKey;  // VNPay
    }
}