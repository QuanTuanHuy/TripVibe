package huy.project.profile_service.ui.kafka;

import huy.project.profile_service.core.domain.constant.TopicConstant;
import huy.project.profile_service.core.domain.dto.kafka.CreateViewHistoryMessage;
import huy.project.profile_service.core.domain.dto.kafka.KafkaBaseDto;
import huy.project.profile_service.core.service.IViewHistoryService;
import huy.project.profile_service.kernel.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@KafkaListener(topics = TopicConstant.ViewHistoryCommand.TOPIC)
@RequiredArgsConstructor
@Component
@Slf4j
public class ViewHistoryMessageHandler {
    private final IViewHistoryService viewHistoryService;
    private final JsonUtils jsonUtils;

    @KafkaHandler
    public void handleMessage(String message) {
        try {
            var kafkaBaseDto = jsonUtils.fromJson(message, KafkaBaseDto.class);
            switch (kafkaBaseDto.getCmd()) {
                case TopicConstant.ViewHistoryCommand.CREATE_VIEW_HISTORY:
                    createViewHistory(kafkaBaseDto.getData());
                    break;
                default:
                    log.error("Unknown command: {}", kafkaBaseDto.getCmd());
            }
        } catch (Exception e) {
            log.error("Failed to handle message: {}, err: {}", message, e.getMessage());
        }

    }

    private void createViewHistory(Object data) {
        String dataStr = jsonUtils.toJson(data);
        CreateViewHistoryMessage req = jsonUtils.fromJson(dataStr, CreateViewHistoryMessage.class);
        viewHistoryService.createViewHistory(req);
    }
}
