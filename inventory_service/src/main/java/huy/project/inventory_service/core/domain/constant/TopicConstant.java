package huy.project.inventory_service.core.domain.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TopicConstant {
    public class BookingCommand {
        public static final String TOPIC = "inventory_service.booking";
        public static final String LOCK_FOR_BOOKING = "lock_for_booking";
        public static final String CONFIRM_BOOKING = "confirm_booking";
        public static final String CANCEL_BOOKING = "cancel_booking";
    }
    
    @UtilityClass
    public class AccommodationCommand {
        public static final String TOPIC = "inventory_service.accommodation";
        public static final String SYNC_ACCOMMODATION = "sync_accommodation";
        public static final String SYNC_UNIT = "sync_unit";
    }
}
