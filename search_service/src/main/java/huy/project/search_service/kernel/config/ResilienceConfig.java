//package huy.project.search_service.kernel.config;
//
//import io.github.resilience4j.circuitbreaker.CircuitBreaker;
//import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
//import io.github.resilience4j.retry.Retry;
//import io.github.resilience4j.retry.RetryRegistry;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@Slf4j
//public class ResilienceConfig {
//
//    public static final String ACCOMMODATION_SERVICE = "accommodationService";
//
//    @Bean
//    public CircuitBreakerRegistry circuitBreakerRegistry() {
//        return CircuitBreakerRegistry.ofDefaults();
//    }
//
//    @Bean
//    public RetryRegistry retryRegistry() {
//        return RetryRegistry.ofDefaults();
//    }
//
//    @Bean
//    public CircuitBreaker accommodationServiceCircuitBreaker(CircuitBreakerRegistry registry) {
//        CircuitBreaker circuitBreaker = registry.circuitBreaker(ACCOMMODATION_SERVICE);
//
//        circuitBreaker.getEventPublisher()
//                .onStateTransition(event -> {
//                    log.info("CircuitBreaker '{}' state changed from {} to {}",
//                            event.getCircuitBreakerName(),
//                            event.getStateTransition().getFromState(),
//                            event.getStateTransition().getToState());
//                })
//                .onCallNotPermitted(event -> {
//                    log.warn("CircuitBreaker '{}' call not permitted",
//                            event.getCircuitBreakerName());
//                });
//
//        return circuitBreaker;
//    }
//
//    @Bean
//    public Retry accommodationServiceRetry(RetryRegistry registry) {
//        Retry retry = registry.retry(ACCOMMODATION_SERVICE);
//
//        retry.getEventPublisher()
//                .onRetry(event -> {
//                    assert event.getLastThrowable() != null;
//                    log.info("Retry '{}' attempt {} for call to '{}'",
//                            event.getName(),
//                            event.getNumberOfRetryAttempts(),
//                            event.getLastThrowable().getMessage());
//                })
//                .onError(event -> {
//                    assert event.getLastThrowable() != null;
//                    log.error("Retry '{}' failed after {} attempts: {}",
//                            event.getName(),
//                            event.getNumberOfRetryAttempts(),
//                            event.getLastThrowable().getMessage());
//                });
//
//        return retry;
//    }
//}
