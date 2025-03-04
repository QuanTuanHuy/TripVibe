package huy.project.accommodation_service.kernel.utils;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class DateTimeUtils {
    public static LocalTime fromSecond(Long seconds) {
        if (seconds == null) {
            return null;
        }
        return LocalTime.ofSecondOfDay(seconds);
    }

    public static Long toSecond(LocalTime time) {
        if (time == null) {
            return null;
        }
        return (long) time.truncatedTo(ChronoUnit.SECONDS).toSecondOfDay();
    }
}
