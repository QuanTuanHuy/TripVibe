package huy.project.accommodation_service.core.domain.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TopicConstant {
    @UtilityClass
    public class BookingCommand {
        public static final String TOPIC = "booking_service.accommodation";
        public static final String CREATE_ACCOMMODATION = "create_accommodation";
    }

    @UtilityClass
    public class ViewHistoryCommand {
        public static final String TOPIC = "tourist_service.view_history";
        public static final String CREATE_VIEW_HISTORY = "create_view_history";
    }
}
