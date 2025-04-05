package huy.project.accommodation_service.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import huy.project.accommodation_service.core.port.IKafkaPublisher;
import huy.project.accommodation_service.kernel.property.KafkaServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("deprecation")
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPublisherAdapter implements IKafkaPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaServer defaultKafkaServer;

    @Override
    public <T> void sendMessageAsync(T data, String topic, String kafkaServer, ListenableFutureCallback<String> callback) {
        String message;
        try {
            message = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Error when convert object to json, {} ", e.getMessage());
            return;
        }
        log.info("===> Sending message=[{}] to topic: {}", message, topic);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);

        if (callback != null) {
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.info("xxxx> Unable to send message=[{}] to topic: {} FAIL !!! \n Due to : {}", message, topic, ex.getMessage(), ex);
                    callback.onFailure(ex);
                } else {
                    log.info("===> Sent message=[{}] with offset=[{}] to topic: {} SUCCESS !!!", message, result.getRecordMetadata().offset(), topic);
                    callback.onSuccess(message);
                }
            });
        }
    }

    @Override
    public <T> void pushAsync(T payload, String topic, String kafkaServer) {
        if (!StringUtils.hasText(kafkaServer)) {
            kafkaServer = defaultKafkaServer.getDefaultServer();
        }
        sendMessageAsync(payload, topic, kafkaServer, null);
    }
}
