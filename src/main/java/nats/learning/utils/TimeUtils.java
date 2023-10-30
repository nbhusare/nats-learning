package nats.learning.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeUtils {

    public static String localTime() {
        return LocalTime.now().toString();
    }

    public static String localDateTime() {
        return LocalDateTime.now().toString();
    }
}
