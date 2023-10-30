package nats.learning;

import io.nats.client.Connection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalTime;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static nats.learning.utils.PrintUtils.*;
import static nats.learning.utils.MessageUtils.*;
import static nats.learning.utils.NatsUtils.*;

public class CoreNatsTests {

    private static Connection CONNECTION;

    @BeforeAll
    static void init() throws IOException, InterruptedException {
        CONNECTION = newConnection();
    }

    @AfterAll
    static void cleanUp() throws IOException, InterruptedException {
        CONNECTION.close();
    }

    @Test
    void testConnection() throws InterruptedException, IOException {
        assertEquals(Connection.Status.CONNECTED, CONNECTION.getStatus());
    }

    @Test
    void testPubSub() throws InterruptedException, IOException {
        //Subscriber 01
        newSubscriber(CONNECTION, (message) -> {
            print("SUB-01, Local time is : {0}", toMsgString(message));
        }).subscribe("time.*");

        //Subscriber 02
        newSubscriber(CONNECTION, (message) -> {
            print("SUB-02, Local time is : {0}", toMsgString(message));
        }).subscribe("time.*");

        // Publish message
        var localTime = LocalTime.now().toString();
        var toSubject = "time.local";
        print("Publishing Local time {0} to Subject {1}",  localTime, toSubject);
        CONNECTION.publish(newMessage(localTime, toSubject));
    }


}
