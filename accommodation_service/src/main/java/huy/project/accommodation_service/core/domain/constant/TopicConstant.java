package huy.project.accommodation_service.core.domain.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TopicConstant {
    @UtilityClass
    public class BookingCommand {
        public static final String TOPIC = "booking_service.accommodation";
        public static final String CREATE_ACCOMMODATION = "create_accommodation";
        public static final String ADD_UNIT_TO_ACC = "add_unit_to_accommodation";
    }

    @UtilityClass
    public class TouristCommand {
        public static final String TOPIC = "tourist_service.view_history";
        public static final String CREATE_VIEW_HISTORY = "create_view_history";
    }

    public class FileCommand {
        public static final String TOPIC = "file_service.file_storage";
        public static final String DELETE_FILE = "delete_file";
    }


    @UtilityClass
    public class SearchCommand {
        public static final String TOPIC = "search_service.accommodation";
        public static final String CREATE_ACCOMMODATION = "create_accommodation";
        public static final String ADD_UNIT_TO_ACC = "add_unit_to_accommodation";
    }

    @UtilityClass
    public class AccommodationEvent {
        public static final String TOPIC = "accommodation_service.event";
        public static final String DELETE_UNIT = "delete_unit";
    }
}
