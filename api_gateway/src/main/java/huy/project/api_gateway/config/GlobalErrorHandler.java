//package huy.project.api_gateway.config;
//
//import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
//import org.springframework.cloud.gateway.support.NotFoundException;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Objects;
//
//@Configuration
//public class GlobalErrorHandler {
//
//    @Bean
//    @Order(-1)
//    public ErrorWebExceptionHandler globalErrorWebExceptionHandler() {
//        return new CustomErrorWebExceptionHandler();
//    }
//
//    public static class CustomErrorWebExceptionHandler implements ErrorWebExceptionHandler {
//
//        @Override
//        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
//            ServerHttpResponse response = exchange.getResponse();
//
//            if (ex instanceof NotFoundException) {
//                response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
//            } else if (ex instanceof java.net.ConnectException ||
//                      ex.getCause() instanceof java.net.ConnectException) {
//                response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
//            } else {
//                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//
//            response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
//            
//            // Extract service name from path
//            String path = exchange.getRequest().getPath().toString();
//            String serviceName = extractServiceName(path);
//
//            String errorResponse = String.format(
//                "{\"meta\":{\"code\":%d001,\"message\":\"%s service is temporarily unavailable. Please try again later.\"},\"data\":null}",
//                Objects.requireNonNull(response.getStatusCode()).value(),
//                serviceName
//            );
//
//            DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
//            return response.writeWith(Flux.just(buffer));
//        }
//
//        private String extractServiceName(String path) {
//            if (path.startsWith("/")) {
//                String[] parts = path.split("/");
//                if (parts.length > 1) {
//                    String servicePart = parts[1];
//                    if (servicePart.endsWith("_service")) {
//                        return servicePart.replace("_service", "").replace("_", " ");
//                    }
//                    return servicePart;
//                }
//            }
//            return "Downstream";
//        }
//    }
//}
