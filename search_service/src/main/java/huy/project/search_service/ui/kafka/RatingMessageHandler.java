package huy.project.search_service.ui.kafka;

import huy.project.search_service.core.domain.constant.TopicConstant;
import huy.project.search_service.core.domain.dto.kafka.KafkaBaseDto;
import huy.project.search_service.core.domain.dto.kafka.SyncRatingMessage;
import huy.project.search_service.core.service.IAccommodationService;
import huy.project.search_service.kernel.utils.JsonUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@KafkaListener(topics = TopicConstant.RatingCommand.TOPIC, groupId = "search_group")
@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RatingMessageHandler {
    IAccommodationService accommodationService;
    JsonUtils jsonUtils;

    @KafkaHandler
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
        try {
            var kafkaBaseDto = jsonUtils.fromJson(message, KafkaBaseDto.class);
            switch (kafkaBaseDto.getCmd()) {
                case TopicConstant.RatingCommand.SYNC_RATING_SUMMARY:
                    handleSyncRating(kafkaBaseDto.getData());
                    break;
                default:
                    log.error("Unknown command: {}", kafkaBaseDto.getCmd());
            }
        } catch (Exception e) {
            log.error("Failed to handle message: {}, err: {}", message, e.getMessage());
        }
    }

    private void handleSyncRating(Object data) {
        String syncRatingMessageJson = jsonUtils.toJson(data);
        var syncRatingMessages = jsonUtils.fromJsonList(syncRatingMessageJson, SyncRatingMessage.class);
        log.info("sync {} rating messages", syncRatingMessages.size());

        for (var syncRatingMessage : syncRatingMessages) {
            try {
                var accommodation = accommodationService.getAccById(syncRatingMessage.getAccommodationId());
                accommodation.setRatingStar(syncRatingMessage.getRating());
                accommodationService.updateAccommodation(accommodation);
            } catch (Exception e) {
                log.error("Failed to update accommodation with id {}: {}", syncRatingMessage.getAccommodationId(), e.getMessage());
            }
        }
    }

}
