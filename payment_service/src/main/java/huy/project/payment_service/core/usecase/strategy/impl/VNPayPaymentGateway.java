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
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VNPayPaymentGateway implements IPaymentGatewayStrategy {
    PaymentProperties paymentProperties;
    ObjectMapper objectMapper;

    @Override
    public PaymentGatewayResult createPayment(CreatePaymentDto requestDto, PaymentEntity payment) {
        try {
            PaymentProperties.GatewayProperties props = paymentProperties.getGateways().get(getGatewayName());

            // Get the terminal ID and secret key
            String vnpTmnCode = props.getTerminalId();
            String vnpHashSecret = props.getSecretKey();

            // Prepare parameters
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", vnpTmnCode);
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", payment.getTransactionId());
            vnpParams.put("vnp_OrderInfo", "Booking ID: " + requestDto.getBookingId());
            vnpParams.put("vnp_OrderType", "billpayment");
            vnpParams.put("vnp_ReturnUrl", requestDto.getReturnUrl() != null
                    ? requestDto.getReturnUrl() : props.getSuccessUrl());

            // Format amount (VNPay requires amount in VND without decimal)
            long amountVND = requestDto.getAmount().longValue();
            vnpParams.put("vnp_Amount", String.valueOf(amountVND));

            // Format date (yyyyMMddHHmmss)
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String createDate = formatter.format(calendar.getTime());
            vnpParams.put("vnp_CreateDate", createDate);

            // Add a random IP address (required by VNPay)
            vnpParams.put("vnp_IpAddr", "127.0.0.1");

            // Build the query string
            List<String> keys = new ArrayList<>(vnpParams.keySet());
            Collections.sort(keys);

            StringBuilder queryBuilder = new StringBuilder();
            for (String key : keys) {
                queryBuilder.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
                queryBuilder.append('=');
                queryBuilder.append(URLEncoder.encode(vnpParams.get(key), StandardCharsets.UTF_8));
                queryBuilder.append('&');
            }
            queryBuilder.deleteCharAt(queryBuilder.length() - 1); // Remove last '&'

            // Create secure hash
            String queryUrl = queryBuilder.toString();
            String vnpSecureHash = hmacSHA512(vnpHashSecret, queryUrl);

            // Build the final URL
            String paymentUrl = UriComponentsBuilder.fromHttpUrl(Gateway.VNP_PAY_URL)
                    .query(queryUrl)
                    .queryParam("vnp_SecureHash", vnpSecureHash)
                    .toUriString();

            return PaymentGatewayResult.builder()
                    .success(true)
                    .gatewayReferenceId(payment.getTransactionId()) // For VNPay, we use our transaction ID
                    .paymentUrl(paymentUrl)
                    .status(PaymentStatus.PENDING)
                    .additionalData(new HashMap<>(vnpParams))
                    .build();

        } catch (Exception e) {
            log.error("Error creating VNPay payment: {}", e.getMessage());
            return PaymentGatewayResult.builder()
                    .success(false)
                    .status(PaymentStatus.FAILED)
                    .message("Failed to create payment with VNPay: " + e.getMessage())
                    .build();
        }
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            sha512Hmac.init(secretKey);
            byte[] hmacData = sha512Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex (hmacData);
        } catch (Exception ex) {
            log.error("Error generating HMAC: {}", ex.getMessage());
            return "";
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Override
    public PaymentGatewayResult processWebhook(PaymentWebhookDto webhookDto, String rawPayload) {
        try {
            Map<String, String> fields = objectMapper.readValue(rawPayload, Map.class);

            // Verify secure hash
            PaymentProperties.GatewayProperties props = paymentProperties.getGateways().get(getGatewayName());
            String secureHash = fields.get("vnp_SecureHash");

            // Remove secure hash for validation
            Map<String, String> validationFields = new HashMap<>(fields);
            validationFields.remove("vnp_SecureHash");
            validationFields.remove("vnp_SecureHashType");

            // Sort the fields
            List<String> keys = new ArrayList<>(validationFields.keySet());
            Collections.sort(keys);

            // Build the query string
            StringBuilder queryBuilder = new StringBuilder();
            for (String key : keys) {
                queryBuilder.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
                queryBuilder.append('=');
                queryBuilder.append(URLEncoder.encode(validationFields.get(key), StandardCharsets.UTF_8));
                queryBuilder.append('&');
            }
            queryBuilder.deleteCharAt(queryBuilder.length() - 1); // Remove last '&'

            // Calculate secure hash
            String calculatedHash = hmacSHA512(props.getSecretKey(), queryBuilder.toString());

            // Verify hash
            if (!calculatedHash.equals(secureHash)) {
                return PaymentGatewayResult.builder()
                        .success(false)
                        .message("Invalid secure hash")
                        .build();
            }

            // Get status
            String vnpResponseCode = fields.get("vnp_ResponseCode");
            String transactionRef = fields.get("vnp_TxnRef");

            PaymentStatus paymentStatus;
            if ("00".equals(vnpResponseCode)) {
                paymentStatus = PaymentStatus.COMPLETED;
            } else if ("24".equals(vnpResponseCode)) {
                paymentStatus = PaymentStatus.CANCELLED;
            } else {
                paymentStatus = PaymentStatus.FAILED;
            }

            return PaymentGatewayResult.builder()
                    .success(true)
                    .gatewayReferenceId(transactionRef)
                    .status(paymentStatus)
                    .rawResponse(rawPayload)
                    .additionalData(new HashMap<>(fields))
                    .build();

        } catch (Exception e) {
            log.error("Error processing VNPay webhook: {}", e.getMessage());
            return PaymentGatewayResult.builder()
                    .success(false)
                    .message("Failed to process VNPay webhook: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PaymentGatewayResult verifyPayment(String gatewayReferenceId) {
        // VNPay doesn't provide a verification API, so we rely on the webhook
        // In a real implementation, you might want to query your database for the transaction
        return PaymentGatewayResult.builder()
                .success(false)
                .message("VNPay does not support payment verification API")
                .build();
    }

    @Override
    public PaymentGatewayResult cancelPayment(PaymentEntity payment) {
        // VNPay doesn't provide a cancellation API
        return PaymentGatewayResult.builder()
                .success(false)
                .message("VNPay does not support payment cancellation API")
                .build();
    }

    @Override
    public String getGatewayName() {
        return Gateway.VNPAY_GATEWAY_NAME;
    }
}
