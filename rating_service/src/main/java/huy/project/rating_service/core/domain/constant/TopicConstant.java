package huy.project.rating_service.core.domain.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TopicConstant {
    @UtilityClass
    public class RatingCommand {
        public static final String TOPIC = "rating_service.rating_summary";
        public static final String CREATE_RATING_SUMMARY = "create_rating_summary";
    }
}
