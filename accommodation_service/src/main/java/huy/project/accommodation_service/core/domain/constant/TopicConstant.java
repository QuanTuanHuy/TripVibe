package huy.project.accommodation_service.core.domain.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TopicConstant {
    @UtilityClass
    public class BookingCommand {
        public static final String TOPIC = "booking_service.accommodation";
        public static final String CREATE_ACCOMMODATION = "create_accommodation";
    }
}
