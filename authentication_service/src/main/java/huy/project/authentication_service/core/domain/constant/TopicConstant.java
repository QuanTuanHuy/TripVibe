package huy.project.authentication_service.core.domain.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TopicConstant {
    @UtilityClass
    public class TouristCommand {
        public static final String TOPIC = "tourist_service.tourist";
        public static final String CREATE_TOURIST = "create_tourist";
    }
}