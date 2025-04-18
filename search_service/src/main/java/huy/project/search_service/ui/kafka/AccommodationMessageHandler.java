package huy.project.search_service.ui.kafka;

import huy.project.search_service.core.domain.constant.TopicConstant;
import huy.project.search_service.core.domain.dto.kafka.AddUnitToAccMessage;
import huy.project.search_service.core.domain.dto.kafka.KafkaBaseDto;
import huy.project.search_service.core.domain.entity.AccommodationEntity;
import huy.project.search_service.core.service.IAccommodationService;
import huy.project.search_service.kernel.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@KafkaListener(topics = TopicConstant.AccommodationCommand.TOPIC, groupId = "search_group")
@RequiredArgsConstructor
@Component
@Slf4j
public class AccommodationMessageHandler {
    private final IAccommodationService accommodationService;
    private final JsonUtils jsonUtils;

    @KafkaHandler
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
        try {
            var kafkaBaseDto = jsonUtils.fromJson(message, KafkaBaseDto.class);
            switch (kafkaBaseDto.getCmd()) {
                case TopicConstant.AccommodationCommand.CREATE_ACCOMMODATION:
                    handleCreateAccommodation(kafkaBaseDto.getData());
                    break;
                case TopicConstant.AccommodationCommand.ADD_UNIT_TO_ACC:
                    handleAddUnitToAccommodation(kafkaBaseDto.getData());
                    break;
                default:
                    log.error("Unknown command: {}", kafkaBaseDto.getCmd());
            }
        } catch (Exception e) {
            log.error("Failed to handle message: {}, err: {}", message, e.getMessage());
        }
    }

    private void handleAddUnitToAccommodation(Object data) {
        String addUnitMessage = jsonUtils.toJson(data);
        var dto = jsonUtils.fromJson(addUnitMessage, AddUnitToAccMessage.class);
        accommodationService.addUnitToAccommodation(dto.getAccommodationId(), dto.getUnit());
    }

    private void handleCreateAccommodation(Object data) {
        String createAccMessage = jsonUtils.toJson(data);
        var accommodation = jsonUtils.fromJson(createAccMessage, AccommodationEntity.class);
        accommodationService.createAccommodation(accommodation);
    }

}
