package huy.project.rating_service.ui.kafka;

import huy.project.rating_service.core.domain.constant.TopicConstant;
import huy.project.rating_service.core.domain.dto.kafka.CreateRatingSummaryMessage;
import huy.project.rating_service.core.domain.dto.kafka.KafkaBaseDto;
import huy.project.rating_service.core.domain.entity.RatingSummaryEntity;
import huy.project.rating_service.core.service.IRatingSummaryService;
import huy.project.rating_service.kernel.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@KafkaListener(topics = TopicConstant.RatingCommand.TOPIC)
@RequiredArgsConstructor
@Component
@Slf4j
public class RatingSummaryMessageHandler {
    private final JsonUtils jsonUtils;
    private final IRatingSummaryService ratingSummaryService;

    @KafkaHandler
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
        try {
            var kafkaBaseDto = jsonUtils.fromJson(message, KafkaBaseDto.class);
            switch (kafkaBaseDto.getCmd()) {
                case TopicConstant.RatingCommand.CREATE_RATING_SUMMARY:
                    handleCreateRatingSummary(kafkaBaseDto.getData());
                    break;
                default:
                    log.error("Unknown command: {}", kafkaBaseDto.getCmd());
            }
        } catch (Exception e) {
            log.error("Failed to handle message: {}, err: {}", message, e.getMessage());
        }
    }

    private void handleCreateRatingSummary(Object data) {
        String jsonData = jsonUtils.toJson(data);
        var accId = jsonUtils.fromJson(jsonData, CreateRatingSummaryMessage.class).getAccommodationId();
        log.info("Received CreateRatingSummaryMessage for accommodationId: {}", accId);

        var ratingSummary = RatingSummaryEntity.builder()
                .accommodationId(accId)
                .isSyncedWithSearchService(false)
                .numberOfRatings(0)
                .totalRating(0L)
                .build();
        ratingSummaryService.createRatingSummary(ratingSummary);
    }
}
