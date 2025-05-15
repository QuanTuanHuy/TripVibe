package com.booking.inventory.application.service;

import com.booking.inventory.domain.service.RoomAvailabilityService;
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
public class BookingEventConsumer {
    
    private final RoomAvailabilityService roomAvailabilityService;
    private final ObjectMapper objectMapper;
    
    @KafkaListener(topics = "booking-events", groupId = "inventory-service")
    public void consumeBookingEvent(String message) {
        try {
            log.info("Received booking event: {}", message);
            JsonNode eventNode = objectMapper.readTree(message);
            
            String eventType = eventNode.get("eventType").asText();
            String bookingId = eventNode.get("bookingId").asText();
            
            switch (eventType) {
                case "BOOKING_CREATED":
                    // No action needed, as the booking was already confirmed through the API
                    break;
                case "BOOKING_CANCELLED":
                    roomAvailabilityService.cancelBooking(bookingId);
                    log.info("Booking cancelled: {}", bookingId);
                    break;
                case "CHECK_IN":
                    roomAvailabilityService.processCheckIn(bookingId);
                    log.info("Check-in processed for booking: {}", bookingId);
                    break;
                case "CHECK_OUT":
                    roomAvailabilityService.processCheckOut(bookingId);
                    log.info("Check-out processed for booking: {}", bookingId);
                    break;
                default:
                    log.warn("Unknown booking event type: {}", eventType);
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing booking event", e);
        }
    }
}
