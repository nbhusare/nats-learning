package nats.learning.utils;

import lombok.extern.slf4j.XSlf4j;

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
