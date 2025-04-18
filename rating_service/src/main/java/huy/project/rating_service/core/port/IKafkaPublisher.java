package huy.project.rating_service.core.port;

import org.springframework.util.concurrent.ListenableFutureCallback;

@SuppressWarnings("deprecation")
public interface IKafkaPublisher {
    <T> void sendMessageAsync(T data, String topic, String kafkaServer, ListenableFutureCallback<String> callback);

    <T> void pushAsync(T payload, String topic, String kafkaServer);
}