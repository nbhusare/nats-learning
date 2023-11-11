package nats.learning.utils;

import io.nats.client.Message;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MessageUtils {

    public static Message newMessage(String data, String subject) {
        return NatsMessage.builder() //
                .subject(subject) //
                .data(data) //
                .build();
    }

    public static Message newMessage(String data, String subject, Headers headers) {
        return NatsMessage.builder() //
                .subject(subject) //
                .data(data) //
                .headers(headers) //
                .build();
    }

    public static Message newMessage(String subject) {
        return NatsMessage.builder() //
                .subject(subject) //
                .build();
    }



    public static String toMsgString(Message message) {
        return new String(message.getData(), UTF_8);
    }
}