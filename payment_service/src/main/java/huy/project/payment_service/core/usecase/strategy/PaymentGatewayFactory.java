package huy.project.payment_service.core.usecase.strategy;

import huy.project.payment_service.kernel.property.PaymentProperties;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentGatewayFactory {
    List<IPaymentGatewayStrategy> gatewayStrategies;
    PaymentProperties paymentProperties;

    @NonFinal
    Map<String, IPaymentGatewayStrategy> gatewayMap;

    @PostConstruct
    public void init() {
        gatewayMap = gatewayStrategies.stream()
                .collect(Collectors.toMap(IPaymentGatewayStrategy::getGatewayName, Function.identity()));
    }

    public IPaymentGatewayStrategy getGateway(String gatewayName) {
        IPaymentGatewayStrategy gateway = gatewayMap.get(gatewayName);
        if (gateway == null) {
            log.warn("Payment gateway not found: {}", gatewayName);
            gateway = getDefaultGateway();
        }
        return gateway;
    }

    public IPaymentGatewayStrategy getDefaultGateway() {
        String defaultGatewayName = paymentProperties.getDefaultGateway();
        var defaultGateway = gatewayMap.get(defaultGatewayName);

        if (defaultGateway == null) {
            log.error("Default gateway '{}' not found! Using first available gateway", defaultGatewayName);
            defaultGateway = gatewayStrategies.stream().findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No payment gateway implementation found!"));
        }

        return defaultGateway;
    }

    public List<String> getAvailableGateways() {
        return gatewayStrategies.stream()
                .map(IPaymentGatewayStrategy::getGatewayName)
                .toList();
    }
}
