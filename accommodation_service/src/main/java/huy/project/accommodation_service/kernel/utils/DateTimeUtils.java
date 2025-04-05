package huy.project.accommodation_service.kernel.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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

    public static LocalDate convertUnixToLocalDate(Long unixTimestamp) {
        if (unixTimestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(unixTimestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
