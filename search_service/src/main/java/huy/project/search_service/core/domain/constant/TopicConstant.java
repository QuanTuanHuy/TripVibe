package huy.project.search_service.core.domain.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TopicConstant {
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
