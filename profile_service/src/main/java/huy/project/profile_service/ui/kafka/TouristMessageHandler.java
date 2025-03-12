package huy.project.profile_service.ui.kafka;

import huy.project.profile_service.core.domain.constant.TopicConstant;
import huy.project.profile_service.core.domain.dto.kafka.CreateTouristMessage;
import huy.project.profile_service.core.domain.dto.kafka.KafkaBaseDto;
import huy.project.profile_service.core.service.ITouristService;
import huy.project.profile_service.kernel.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@KafkaListener(topics = TopicConstant.TouristCommand.TOPIC)
@RequiredArgsConstructor
@Component
@Slf4j
public class TouristMessageHandler {
    private final ITouristService touristService;
    private final JsonUtils jsonUtils;

    @KafkaHandler
    public void handleMessage(String message) {
        try {
            var kafkaBaseDto = jsonUtils.fromJson(message, KafkaBaseDto.class);
            switch (kafkaBaseDto.getCmd()) {
                case TopicConstant.TouristCommand.CREATE_TOURIST:
                    String data = jsonUtils.toJson(kafkaBaseDto.getData());
                    CreateTouristMessage dto = jsonUtils.fromJson(data, CreateTouristMessage.class);
                    touristService.createTourist(dto.getUserId(), dto.getEmail());
                    break;
                default:
                    log.error("Unknown command: {}", kafkaBaseDto.getCmd());
            }
        } catch (Exception e) {
            log.error("Failed to handle message: {}, err: {}", message, e.getMessage());
        }

    }
}
