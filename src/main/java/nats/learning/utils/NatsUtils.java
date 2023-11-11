package nats.learning.utils;

import io.nats.client.*;

import java.io.IOException;

public class NatsUtils {

    public static Connection newConnection() throws IOException, InterruptedException {
        return Nats.connect();
    }

    public static Connection newConnection(Options options) throws IOException, InterruptedException {
        return Nats.connect(options);
    }

    public static Dispatcher newSubscriber(Connection connection, MessageHandler messageHandler) {
        return connection.createDispatcher(messageHandler);
    }

    public static Dispatcher newSubscriber(Connection connection) {
        return connection.createDispatcher();
    }

}
