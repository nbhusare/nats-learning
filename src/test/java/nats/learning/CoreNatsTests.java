package nats.learning;

import io.nats.client.Connection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nats.learning.utils.MessageUtils.newMessage;
import static nats.learning.utils.MessageUtils.toMsgString;
import static nats.learning.utils.NatsUtils.newConnection;
import static nats.learning.utils.NatsUtils.newSubscriber;
import static nats.learning.utils.PrintUtils.print;
import static nats.learning.utils.TimeUtils.localDateTime;
import static nats.learning.utils.TimeUtils.localTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoreNatsTests {

    private static Connection CONNECTION;

    @BeforeAll
    static void init() throws IOException, InterruptedException {
        CONNECTION = newConnection();
    }

    @AfterAll
    static void cleanUp() throws IOException, InterruptedException, TimeoutException {
        CONNECTION.flush(Duration.ZERO);
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

        // Publish local time to
        var localTime = localTime();
        var toSubject = "time.local";
        print("Publishing Local time {0} to Subject {1}", localTime, toSubject);
        CONNECTION.publish(newMessage(localTime, toSubject));
    }

    @Test
    void testRequestReply() throws InterruptedException, IOException {
        newSubscriber(CONNECTION, (message) -> {
            var inbox = message.getReplyTo();
            CONNECTION.publish(inbox, localTime().getBytes(UTF_8));
        }).subscribe("time.local");

        // Publish message
        print("Requesting the local time");
        var subject = "time.local";
        var localTime = CONNECTION.request(newMessage(subject), Duration.ofSeconds(1));
        print("Local time is : {0}", toMsgString(localTime));
    }

    @Test
    void testScatterGather() throws InterruptedException, IOException {
        newSubscriber(CONNECTION, (message) -> {
            var replyToSubject = message.getReplyTo();
            CONNECTION.publish(replyToSubject, localTime().getBytes(UTF_8));
        }).subscribe("time.any");

        newSubscriber(CONNECTION, (message) -> {
            var replyToSubject = message.getReplyTo();
            CONNECTION.publish(replyToSubject, localDateTime().getBytes(UTF_8));
        }).subscribe("time.any");

        newSubscriber(CONNECTION, (message) -> {
            print("SUB-03, Received data : {0}", toMsgString(message));
        }).subscribe("time.all");

        // Publish message
        CONNECTION.publish("time.any", "time.all", "".getBytes(UTF_8));
    }

    @Test
    void testQueueGroup() throws InterruptedException, IOException {
        var queueGroup = "time.local";

        //Subscriber 01
        newSubscriber(CONNECTION, (message) -> {
            print("SUB-01, Local time is : {0}", toMsgString(message));
        }).subscribe("time.*", queueGroup);

        // Publish local time to
        for (int x = 1; x <= 1000; x++) {
            CONNECTION.publish("time.any", localTime().getBytes(UTF_8));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
    }
}
