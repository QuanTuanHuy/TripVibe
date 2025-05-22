package huy.project.inventory_service.core.port;

import huy.project.inventory_service.core.domain.callback.KafkaOperationCallback;

public interface IKafkaPublisher {
    <T> void sendMessageAsync(T data, String topic, String kafkaServer, KafkaOperationCallback callback);
    
    <T> void pushAsync(T payload, String topic, String kafkaServer);
}
