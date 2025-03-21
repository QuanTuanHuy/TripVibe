package huy.project.search_service.ui.kafka;

import huy.project.search_service.core.domain.constant.TopicConstant;
import huy.project.search_service.core.domain.dto.kafka.DomainEventDto;
import huy.project.search_service.core.domain.dto.request.DeleteUnitDto;
import huy.project.search_service.core.service.IAccommodationService;
import huy.project.search_service.kernel.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@KafkaListener(topics = TopicConstant.AccommodationEvent.TOPIC)
@RequiredArgsConstructor
@Component
@Slf4j
public class AccommodationEventHandler {
    private final IAccommodationService accommodationService;
    private final JsonUtils jsonUtils;


    @KafkaHandler
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
        try {
            var domainEventDto = jsonUtils.fromJson(message, DomainEventDto.class);
            switch (domainEventDto.getEventType()) {
                case TopicConstant.AccommodationEvent.DELETE_UNIT:
                    handleDeleteUnit(domainEventDto.getData());
                    break;
                default:
                    log.error("Unknown event: {}", domainEventDto.getEventType());
            }
        } catch (Exception e) {
            log.error("Failed to handle message: {}, err: {}", message, e.getMessage());
        }
    }

    private void handleDeleteUnit(Object data) {
        String deleteUnitStr = jsonUtils.toJson(data);
        var dto = jsonUtils.fromJson(deleteUnitStr, DeleteUnitDto.class);
        accommodationService.deleteUnit(dto.getAccommodationId(), dto.getUnitId());
    }
}
