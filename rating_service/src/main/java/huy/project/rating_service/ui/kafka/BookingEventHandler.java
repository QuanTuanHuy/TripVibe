package huy.project.rating_service.ui.kafka;

import huy.project.rating_service.core.domain.constant.TopicConstant;
import huy.project.rating_service.core.domain.dto.response.BookingDetailsDto;
import huy.project.rating_service.core.domain.entity.PendingReviewEntity;
import huy.project.rating_service.core.domain.event.BookingCompletedEvent;
import huy.project.rating_service.core.domain.event.DomainEventDto;
import huy.project.rating_service.core.service.IBookingService;
import huy.project.rating_service.core.service.IPendingReviewService;
import huy.project.rating_service.kernel.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@KafkaListener(topics = TopicConstant.BookingEvent.TOPIC)
@RequiredArgsConstructor
@Component
@Slf4j
public class BookingEventHandler {
    private final JsonUtils jsonUtils;
    private final IPendingReviewService pendingReviewService;
    private final IBookingService bookingService;

    @KafkaHandler
    public void handleMessage(String message) {
        log.info("Received message: {}", message);
        try {
            var bookingEvent = jsonUtils.fromJson(message, DomainEventDto.class);
            switch (bookingEvent.getEventType()) {
                case TopicConstant.BookingEvent.BOOKING_COMPLETED:
                    handleCreatePendingReview(bookingEvent.getData());
                    break;
                default:
                    log.error("Unknown event type: {}", bookingEvent.getEventType());
            }
        } catch (Exception e) {
            log.error("Failed to handle message: {}, err: {}", message, e.getMessage());
        }
    }

    private void handleCreatePendingReview(Object data) {
        String eventData = jsonUtils.toJson(data);
        BookingCompletedEvent event = jsonUtils.fromJson(eventData, BookingCompletedEvent.class);

        try {
            BookingDetailsDto booking = bookingService.getBookingById(event.getId());
            booking.getUnits().forEach(unit -> {
                var pendingReview = PendingReviewEntity.builder()
                        .bookingId(event.getId())
                        .accommodationId(booking.getAccommodationId())
                        .unitId(unit.getUnitId())
                        .userId(booking.getTouristId())
                        .build();
                pendingReviewService.createPendingReview(pendingReview);
            });
        } catch (Exception e) {
            log.error("Error when getting booking details for id {}: {}", event.getId(), e.getMessage());
        }

    }

}
