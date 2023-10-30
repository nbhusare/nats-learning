package nats.learning.utils;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.MessageHandler;
import io.nats.client.Nats;

import java.io.IOException;

public class NatsUtils {

    public static Connection newConnection() throws IOException, InterruptedException {
        return Nats.connect();
    }

    public static Dispatcher newSubscriber(Connection connection, MessageHandler messageHandler) {
        return connection.createDispatcher(messageHandler);
    }
}
