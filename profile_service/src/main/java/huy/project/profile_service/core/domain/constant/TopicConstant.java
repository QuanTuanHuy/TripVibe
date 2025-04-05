package huy.project.profile_service.core.domain.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TopicConstant {
    @UtilityClass
    public class TouristCommand {
        public static final String TOPIC = "tourist_service.tourist";
        public static final String CREATE_TOURIST = "create_tourist";
    }

    @UtilityClass
    public class ViewHistoryCommand {
        public static final String TOPIC = "tourist_service.view_history";
        public static final String CREATE_VIEW_HISTORY = "create_view_history";
    }
}
