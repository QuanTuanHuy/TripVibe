package huy.project.file_service.ui.kafka;

import huy.project.file_service.core.domain.constant.TopicConstant;
import huy.project.file_service.core.domain.dto.kafka.DeleteFileMessage;
import huy.project.file_service.core.domain.dto.kafka.KafkaBaseDto;
import huy.project.file_service.core.service.IFileStorageService;
import huy.project.file_service.kernel.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@KafkaListener(topics = TopicConstant.FileCommand.TOPIC)
@RequiredArgsConstructor
@Component
@Slf4j
public class FileMessageHandler {
    private final IFileStorageService fileStorageService;
    private final JsonUtils jsonUtils;

    @KafkaHandler
    public void handleMessage(String message) {
        log.info("Message received: {}", message);
        try {
            var kafkaBaseDto = jsonUtils.fromJson(message, KafkaBaseDto.class);
            switch (kafkaBaseDto.getCmd()) {
                case TopicConstant.FileCommand.DELETE_FILE:
                    handleDeleteFileMessage(kafkaBaseDto.getData());
                    break;
                default:
                    log.error("Unknown command: {}", kafkaBaseDto.getCmd());
            }
        } catch (Exception e) {
            log.error("Failed to handle message: {}, err: {}", message, e.getMessage());
        }
    }

    private void handleDeleteFileMessage(Object data) {
        String dataJson = jsonUtils.toJson(data);
        var dto = jsonUtils.fromJson(dataJson, DeleteFileMessage.class);
        fileStorageService.deleteFiles(dto.getUserId(), dto.getIds());
    }
}
