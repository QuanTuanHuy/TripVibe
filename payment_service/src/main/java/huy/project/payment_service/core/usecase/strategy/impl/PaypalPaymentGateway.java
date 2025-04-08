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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaypalPaymentGateway implements IPaymentGatewayStrategy {
    PaymentProperties paymentProperties;
    ObjectMapper objectMapper;
    RestTemplate restTemplate;

    @Override
    public PaymentGatewayResult createPayment(CreatePaymentDto requestDto, PaymentEntity payment) {
        try {
            PaymentProperties.GatewayProperties props = paymentProperties.getGateways().get(Gateway.PAYPAL_GATEWAY_NAME);

            // Step 1: Get access token
            String accessToken = getAccessToken(props);

            // Step 2: Create payment order
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Prepare request body
            Map<String, Object> orderRequest = new HashMap<>();
            orderRequest.put("intent", "CAPTURE");

            // Add purchase units
            Map<String, Object> amount = new HashMap<>();
            amount.put("currency_code", requestDto.getCurrency());
            amount.put("value", requestDto.getAmount().toString());

            Map<String, Object> purchaseUnit = new HashMap<>();
            purchaseUnit.put("amount", amount);
            purchaseUnit.put("description", "Booking ID: " + requestDto.getBookingId());

            // Add reference ID (our transaction ID)
            purchaseUnit.put("reference_id", payment.getTransactionId());

            orderRequest.put("purchase_units", List.of(purchaseUnit));

            // Add redirect URLs
            Map<String, String> applicationContext = new HashMap<>();
            applicationContext.put("return_url", requestDto.getReturnUrl() != null
                    ? requestDto.getReturnUrl() : props.getSuccessUrl());
            applicationContext.put("cancel_url", requestDto.getCancelUrl() != null
                    ? requestDto.getCancelUrl() : props.getCancelUrl());
            orderRequest.put("application_context", applicationContext);

            // Create order
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderRequest, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.paypal.com/v2/checkout/orders", request, String.class);

            // Parse response
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
            String orderId = (String) responseMap.get("id");
            List<Map<String, Object>> links = (List<Map<String, Object>>) responseMap.get("links");

            // Find approval URL
            String approvalUrl = links.stream()
                    .filter(link -> "approve".equals(link.get("rel")))
                    .findFirst()
                    .map(link -> (String) link.get("href"))
                    .orElse(null);

            if (approvalUrl == null) {
                return PaymentGatewayResult.builder()
                        .success(false)
                        .message("PayPal approval URL not found in response")
                        .build();
            }

            return PaymentGatewayResult.builder()
                    .success(true)
                    .gatewayReferenceId(orderId)
                    .paymentUrl(approvalUrl)
                    .status(PaymentStatus.PENDING)
                    .rawResponse(response.getBody())
                    .additionalData(responseMap)
                    .build();

        } catch (Exception e) {
            log.error("Error creating PayPal payment: {}", e.getMessage());
            return PaymentGatewayResult.builder()
                    .success(false)
                    .status(PaymentStatus.FAILED)
                    .message("Failed to create payment with PayPal: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentGatewayResult processWebhook(PaymentWebhookDto webhookDto, String rawPayload) {
        try {
            // In a real implementation, verify the webhook signature with PayPal
            // Parse the webhook event
            Map<String, Object> eventData = objectMapper.readValue(rawPayload, Map.class);
            String eventType = (String) eventData.get("event_type");
            Map<String, Object> resource = (Map<String, Object>) eventData.get("resource");

            String paymentId = (String) resource.get("id");
            String status = (String) resource.get("status");

            PaymentStatus paymentStatus;
            switch (status) {
                case "COMPLETED":
                    paymentStatus = PaymentStatus.COMPLETED;
                    break;
                case "APPROVED":
                case "CREATED":
                    paymentStatus = PaymentStatus.PENDING;
                    break;
                case "VOIDED":
                case "CANCELLED":
                    paymentStatus = PaymentStatus.CANCELLED;
                    break;
                default:
                    paymentStatus = PaymentStatus.FAILED;
            }

            return PaymentGatewayResult.builder()
                    .success(true)
                    .gatewayReferenceId(paymentId)
                    .status(paymentStatus)
                    .rawResponse(rawPayload)
                    .additionalData(eventData)
                    .build();

        } catch (Exception e) {
            log.error("Error processing PayPal webhook: {}", e.getMessage());
            return PaymentGatewayResult.builder()
                    .success(false)
                    .message("Failed to process PayPal webhook: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentGatewayResult verifyPayment(String gatewayReferenceId) {
        try {
            PaymentProperties.GatewayProperties props = paymentProperties.getGateways().get(Gateway.PAYPAL_GATEWAY_NAME);

            // Get access token
            String accessToken = getAccessToken(props);

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            // Send request to PayPal
            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.paypal.com/v2/checkout/orders/" + gatewayReferenceId,
                    HttpMethod.GET, request, String.class);

            // Parse response
            Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
            String status = (String) responseMap.get("status");

            PaymentStatus paymentStatus;
            switch (status) {
                case "COMPLETED":
                    paymentStatus = PaymentStatus.COMPLETED;
                    break;
                case "APPROVED":
                case "CREATED":
                    paymentStatus = PaymentStatus.PENDING;
                    break;
                case "VOIDED":
                case "CANCELLED":
                    paymentStatus = PaymentStatus.CANCELLED;
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
            log.error("Error verifying PayPal payment: {}", e.getMessage());
            return PaymentGatewayResult.builder()
                    .success(false)
                    .message("Failed to verify payment with PayPal: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentGatewayResult cancelPayment(PaymentEntity payment) {
        try {
            PaymentProperties.GatewayProperties props = paymentProperties.getGateways().get(Gateway.PAYPAL_GATEWAY_NAME);

            // Get access token
            String accessToken = getAccessToken(props);

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            // Send request to PayPal
            HttpEntity<String> request = new HttpEntity<>(headers);
            restTemplate.exchange(
                    "https://api.paypal.com/v2/checkout/orders/" + payment.getGatewayReferenceId() + "/cancel",
                    HttpMethod.POST, request, String.class);

            // If no exception, assume success
            return PaymentGatewayResult.builder()
                    .success(true)
                    .gatewayReferenceId(payment.getGatewayReferenceId())
                    .status(PaymentStatus.CANCELLED)
                    .build();

        } catch (Exception e) {
            log.error("Error cancelling PayPal payment: {}", e.getMessage());
            return PaymentGatewayResult.builder()
                    .success(false)
                    .message("Failed to cancel payment with PayPal: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public String getGatewayName() {
        return Gateway.PAYPAL_GATEWAY_NAME;
    }

    private String getAccessToken(PaymentProperties.GatewayProperties props) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Basic auth with client ID and secret
        String auth = props.getClientId() + ":" + props.getClientSecret();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()));

        // Prepare body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        // Send request
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.paypal.com/v1/oauth2/token", request, Map.class);

        return (String) response.getBody().get("access_token");
    }
}
