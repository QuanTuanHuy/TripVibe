package huy.project.rating_service.core.domain.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TopicConstant {
    @UtilityClass
    public class RatingCommand {
        public static final String TOPIC = "rating_service.rating_summary";
        public static final String CREATE_RATING_SUMMARY = "create_rating_summary";
    }

    @UtilityClass
    public class SearchCommand {
        public static final String TOPIC = "search_service.rating_summary";
        public static final String SYNC_RATING_SUMMARY = "sync_rating_summary";
    }

    public class BookingEvent {
        public static final String TOPIC = "booking_service.booking_event";
        public static final String BOOKING_COMPLETED = "booking_completed";
    }
}
