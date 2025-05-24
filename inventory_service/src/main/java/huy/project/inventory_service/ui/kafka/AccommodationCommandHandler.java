package huy.project.inventory_service.ui.kafka;

import huy.project.inventory_service.core.domain.constant.TopicConstant;
import huy.project.inventory_service.core.domain.dto.kafka.KafkaBaseDto;
import huy.project.inventory_service.core.domain.dto.request.SyncAccommodationDto;
import huy.project.inventory_service.core.domain.dto.request.SyncUnitDto;
import huy.project.inventory_service.core.service.IAccommodationService;
import huy.project.inventory_service.core.service.IUnitService;
import huy.project.inventory_service.kernel.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@KafkaListener(topics = TopicConstant.AccommodationCommand.TOPIC, groupId = "${spring.kafka.consumer.group-id}")
@RequiredArgsConstructor
@Component
@Slf4j
public class AccommodationCommandHandler {
    private final IAccommodationService accommodationService;
    private final IUnitService unitService;
    private final JsonUtils jsonUtils;

    @KafkaHandler
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
        try {
            KafkaBaseDto kafkaBaseDto = jsonUtils.fromJson(message, KafkaBaseDto.class);

            switch (kafkaBaseDto.getCmd()) {
                // Example handlers for different commands
                case TopicConstant.AccommodationCommand.SYNC_ACCOMMODATION:
                    handleSyncAccommodation(kafkaBaseDto.getData());
                    break;
                case TopicConstant.AccommodationCommand.SYNC_UNIT:
                    handleSyncUnit(kafkaBaseDto.getData());
                    break;
                default:
                    log.warn("Unknown command: {}", kafkaBaseDto.getCmd());
            }
        } catch (Exception e) {
            log.error("Failed to process message: {}, error: {}", message, e.getMessage(), e);
        }
    }

    private void handleSyncUnit(Object data) {
        String dataStr = jsonUtils.toJson(data);
        log.info("sync unit data in inventory service, data: {}", dataStr);

        SyncUnitDto req = jsonUtils.fromJson(dataStr, SyncUnitDto.class);
        var unit = unitService.syncUnit(req);
        if (unit != null) {
            log.info("sync unit success, unit: {}", unit);
        } else {
            log.error("sync unit failed, data: {}", dataStr);
        }

    }

    private void handleSyncAccommodation(Object data) {
        String dataStr = jsonUtils.toJson(data);
        log.info("sync accommodation data in inventory service, data: {}", dataStr);

        SyncAccommodationDto req = jsonUtils.fromJson(dataStr, SyncAccommodationDto.class);
        var accommodation = accommodationService.syncAccommodation(req);
        if (accommodation != null) {
            log.info("sync accommodation success, accommodation: {}", accommodation);
        } else {
            log.error("sync accommodation failed, data: {}", dataStr);
        }
    }
}
