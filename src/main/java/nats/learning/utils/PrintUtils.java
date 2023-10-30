package nats.learning.utils;

import java.text.MessageFormat;

public class PrintUtils {
    public static void print(String message, Object ... arguments) {
        System.out.println(MessageFormat.format(message, arguments));
    }
}
