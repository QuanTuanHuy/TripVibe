package com.booking.inventory.application.service;

import com.booking.inventory.application.dto.UnitInventorySyncRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccommodationEventConsumer {

    private final UnitInventoryApplicationService unitInventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topic.accommodation-events}", groupId = "${app.kafka.consumer.group-id}")
    public void consumeAccommodationEvent(String message) {
        log.info("Received accommodation event: {}", message);
        
        try {
            JsonNode eventNode = objectMapper.readTree(message);
            String eventType = eventNode.path("eventType").asText();
            JsonNode payload = eventNode.path("payload");
            
            switch (eventType) {
                case "UNIT_CREATED":
                case "UNIT_UPDATED":
                    handleUnitEvent(payload);
                    break;
                case "UNIT_DELETED":
                    handleUnitDeletedEvent(payload);
                    break;
                default:
                    log.warn("Unhandled event type: {}", eventType);
                    break;
            }
            
        } catch (JsonProcessingException e) {
            log.error("Error parsing accommodation event: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error processing accommodation event: {}", e.getMessage(), e);
        }
    }
    
    private void handleUnitEvent(JsonNode payload) {
        try {
            Long accommodationId = payload.path("accommodationId").asLong();
            Long unitId = payload.path("unitId").asLong();
            String unitName = payload.path("name").asText();
            Integer quantity = payload.path("quantity").asInt();
            Long roomTypeId = payload.path("roomTypeId").asLong();
            
            UnitInventorySyncRequest syncRequest = UnitInventorySyncRequest.builder()
                    .accommodationId(accommodationId)
                    .unitId(unitId)
                    .unitName(unitName)
                    .quantity(quantity)
                    .roomTypeId(roomTypeId)
                    .build();
                    
            unitInventoryService.syncUnitInventory(syncRequest);
            log.info("Successfully synchronized unit: {}", unitId);
            
        } catch (Exception e) {
            log.error("Error handling unit event: {}", e.getMessage(), e);
        }
    }
    
    private void handleUnitDeletedEvent(JsonNode payload) {
        // In a real application, you would implement soft deletion logic here
        // For now, we'll just log the event
        Long unitId = payload.path("unitId").asLong();
        log.info("Unit deletion event received for unit: {}. Implementation needed.", unitId);
    }
}
