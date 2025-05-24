package huy.project.inventory_service.ui.kafka;

import huy.project.inventory_service.core.domain.constant.TopicConstant;
import huy.project.inventory_service.core.domain.dto.kafka.KafkaBaseDto;
import huy.project.inventory_service.core.service.IInventoryService;
import huy.project.inventory_service.kernel.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@KafkaListener(topics = TopicConstant.BookingCommand.TOPIC, groupId = "${spring.kafka.consumer.group-id}")
@RequiredArgsConstructor
@Component
@Slf4j
public class BookingCommandHandler {
    
    private final IInventoryService inventoryService;
    private final JsonUtils jsonUtils;
    
    @KafkaHandler
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
        try {
            KafkaBaseDto kafkaBaseDto = jsonUtils.fromJson(message, KafkaBaseDto.class);
            
            switch (kafkaBaseDto.getCmd()) {
                // Example handlers for different commands
                case TopicConstant.BookingCommand.LOCK_FOR_BOOKING:
                    handleLockInventory(kafkaBaseDto.getData());
                    break;
                case TopicConstant.BookingCommand.CONFIRM_BOOKING:
                    handleConfirmBooking(kafkaBaseDto.getData());
                    break;
                case TopicConstant.BookingCommand.CANCEL_BOOKING:
                    handleCancelBooking(kafkaBaseDto.getData());
                    break;
                default:
                    log.warn("Unknown command: {}", kafkaBaseDto.getCmd());
            }
        } catch (Exception e) {
            log.error("Failed to process message: {}, error: {}", message, e.getMessage(), e);
        }
    }
    
    private void handleLockInventory(Object data) {
        // Implementation for handling lock inventory command
        log.info("Processing lock inventory command with data: {}", data);
    }
    
    private void handleConfirmBooking(Object data) {
        // Implementation for handling confirm booking command
        log.info("Processing confirm booking command with data: {}", data);
    }
    
    private void handleCancelBooking(Object data) {
        // Implementation for handling cancel booking command
        log.info("Processing cancel booking command with data: {}", data);
    }
}
