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

import java.util.HashMap;

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

        var distribution = new HashMap<Integer, Integer>() {{
            put(1, 0);
            put(2, 0);
            put(3, 0);
            put(4, 0);
            put(5, 0);
            put(6, 0);
            put(7, 0);
            put(8, 0);
            put(9, 0);
            put(10, 0);
        }};

        var ratingSummary = RatingSummaryEntity.builder()
                .accommodationId(accId)
                .isSyncedWithSearchService(false)
                .numberOfRatings(0)
                .totalRating(0L)
                .distribution(distribution)
                .criteriaAverages(new HashMap<>())
                .build();
        ratingSummaryService.createRatingSummary(ratingSummary);
    }
}
