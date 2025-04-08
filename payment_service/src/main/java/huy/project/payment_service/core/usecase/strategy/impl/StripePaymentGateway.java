package huy.project.payment_service.core.usecase.strategy.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import huy.project.payment_service.core.domain.constant.Gateway;
import huy.project.payment_service.core.domain.dto.PaymentWebhookDto;
import huy.project.payment_service.core.domain.dto.request.CreatePaymentDto;
import huy.project.payment_service.core.domain.dto.response.PaymentGatewayResult;
import huy.project.payment_service.core.domain.entity.PaymentEntity;
import huy.project.payment_service.core.domain.enums.PaymentStatus;
import huy.project.payment_service.core.usecase.strategy.IPaymentGatewayStrategy;
import huy.project.payment_service.kernel.property.PaymentProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripePaymentGateway implements IPaymentGatewayStrategy {
    PaymentProperties paymentProperties;
    RestTemplate restTemplate;
    ObjectMapper objectMapper;

    @Override
    public PaymentGatewayResult createPayment(CreatePaymentDto requestDto, PaymentEntity payment) {
        try {
            PaymentProperties.GatewayProperties props = paymentProperties.getGateways().get(getGatewayName());

            // Prepare headers with Stripe API key
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + props.getApiKey());

            // Prepare request body
            Map<String, Object> paymentIntent = new HashMap<>();
            paymentIntent.put("amount", requestDto.getAmount().multiply(java.math.BigDecimal.valueOf(100)).intValue()); // Stripe uses cents
            paymentIntent.put("currency", requestDto.getCurrency().toLowerCase());
            paymentIntent.put("payment_method_types", new String[]{"card"});
            paymentIntent.put("description", "Booking ID: " + requestDto.getBookingId());

            // Add metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("bookingId", requestDto.getBookingId().toString());
            metadata.put("transactionId", payment.getTransactionId());
            paymentIntent.put("metadata", metadata);

            // Create redirect URLs
            Map<String, Object> redirectUrls = new HashMap<>();
            redirectUrls.put("success_url", requestDto.getReturnUrl() != null
                    ? requestDto.getReturnUrl() : props.getSuccessUrl());
            redirectUrls.put("cancel_url", requestDto.getCancelUrl() != null
                    ? requestDto.getCancelUrl() : props.getCancelUrl());
            paymentIntent.put("redirect_urls", redirectUrls);

            // Send request to Stripe
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(paymentIntent, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.stripe.com/v1/payment_intents", request, String.class);

            // Parse response
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
            String clientSecret = (String) responseMap.get("client_secret");
            String intentId = (String) responseMap.get("id");

            // Generate payment URL for redirect
            String paymentUrl = "https://checkout.stripe.com/pay/" + clientSecret;

            return PaymentGatewayResult.builder()
                    .success(true)
                    .gatewayReferenceId(intentId)
                    .paymentUrl(paymentUrl)
                    .status(PaymentStatus.PENDING)
                    .rawResponse(response.getBody())
                    .additionalData(responseMap)
                    .build();

        } catch (Exception e) {
            log.error("Error creating Stripe payment: {}", e.getMessage());
            return PaymentGatewayResult.builder()
                    .success(false)
                    .status(PaymentStatus.FAILED)
                    .message("Failed to create payment with Stripe: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentGatewayResult processWebhook(PaymentWebhookDto webhookDto, String rawPayload) {
        try {
            // In a real implementation, verify the webhook signature with Stripe
            // Parse the webhook event
            Map<String, Object> eventData = objectMapper.readValue(rawPayload, Map.class);
            String eventType = (String) eventData.get("type");
            Map<String, Object> dataObject = (Map<String, Object>) eventData.get("data");
            Map<String, Object> object = (Map<String, Object>) dataObject.get("object");

            String paymentIntentId = (String) object.get("id");
            String status = (String) object.get("status");

            PaymentStatus paymentStatus;
            switch (status) {
                case "succeeded":
                    paymentStatus = PaymentStatus.COMPLETED;
                    break;
                case "canceled":
                    paymentStatus = PaymentStatus.CANCELLED;
                    break;
                case "requires_payment_method":
                case "requires_confirmation":
                case "requires_action":
                case "processing":
                    paymentStatus = PaymentStatus.PENDING;
                    break;
                default:
                    paymentStatus = PaymentStatus.FAILED;
            }

            return PaymentGatewayResult.builder()
                    .success(true)
                    .gatewayReferenceId(paymentIntentId)
                    .status(paymentStatus)
                    .rawResponse(rawPayload)
                    .additionalData(eventData)
                    .build();

        } catch (Exception e) {
            log.error("Error processing Stripe webhook: {}", e.getMessage());
            return PaymentGatewayResult.builder()
                    .success(false)
                    .message("Failed to process Stripe webhook: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentGatewayResult verifyPayment(String gatewayReferenceId) {
        try {
            PaymentProperties.GatewayProperties props = paymentProperties.getGateways().get(getGatewayName());

            // Prepare headers with Stripe API key
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + props.getApiKey());

            // Send request to Stripe
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.stripe.com/v1/payment_intents/" + gatewayReferenceId,
                    org.springframework.http.HttpMethod.GET, request, String.class);

            // Parse response
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
            String status = (String) responseMap.get("status");

            PaymentStatus paymentStatus;
            switch (status) {
                case "succeeded":
                    paymentStatus = PaymentStatus.COMPLETED;
                    break;
                case "canceled":
                    paymentStatus = PaymentStatus.CANCELLED;
                    break;
                case "requires_payment_method":
                case "requires_confirmation":
                case "requires_action":
                case "processing":
                    paymentStatus = PaymentStatus.PENDING;
                    break;
                default:
                    paymentStatus = PaymentStatus.FAILED;
            }

            return PaymentGatewayResult.builder()
                    .success(true)
                    .gatewayReferenceId(gatewayReferenceId)
                    .status(paymentStatus)
                    .rawResponse(response.getBody())
                    .build();

        } catch (Exception e) {
            log.error("Error verifying Stripe payment: {}", e.getMessage());
            return PaymentGatewayResult.builder()
                    .success(false)
                    .message("Failed to verify payment with Stripe: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentGatewayResult cancelPayment(PaymentEntity payment) {
        try {
            PaymentProperties.GatewayProperties props = paymentProperties.getGateways().get(getGatewayName());

            // Prepare headers with Stripe API key
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + props.getApiKey());

            // Send cancel request to Stripe
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.stripe.com/v1/payment_intents/" + payment.getGatewayReferenceId() + "/cancel",
                    request, String.class);

            // Parse response
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
            String status = (String) responseMap.get("status");

            boolean cancelled = "canceled".equals(status);

            return PaymentGatewayResult.builder()
                    .success(cancelled)
                    .gatewayReferenceId(payment.getGatewayReferenceId())
                    .status(cancelled ? PaymentStatus.CANCELLED : payment.getStatus())
                    .rawResponse(response.getBody())
                    .build();

        } catch (Exception e) {
            log.error("Error cancelling Stripe payment: {}", e.getMessage());
            return PaymentGatewayResult.builder()
                    .success(false)
                    .message("Failed to cancel payment with Stripe: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public String getGatewayName() {
        return Gateway.STRIPE_GATEWAY_NAME;
    }
}
