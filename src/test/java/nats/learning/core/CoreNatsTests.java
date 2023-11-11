package nats.learning.core;

import io.nats.client.Connection;
import io.nats.client.impl.Headers;
import lombok.extern.slf4j.Slf4j;
import nats.learning.ext.TimingExtension;
import nats.learning.ext.annotations.NatsConnection;
import nats.learning.ext.annotations.NatsServerAndConnectionExtension;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nats.learning.utils.MessageUtils.newMessage;
import static nats.learning.utils.MessageUtils.toMsgString;
import static nats.learning.utils.NatsUtils.newSubscriber;
import static nats.learning.utils.TimeUtils.localDateTime;
import static nats.learning.utils.TimeUtils.localTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test to showcase the Core NATS functionalities - Pub/Sub, Request-Reply, and Queue groups.
 */
@NatsServerAndConnectionExtension
@ExtendWith(TimingExtension.class)

@Slf4j
public class CoreNatsTests {

    public static final String QUEUE_GROUP_NAME = "time.local";

    @Test
    void testConnection(@NatsConnection Connection connection)  {
        assertEquals(Connection.Status.CONNECTED, connection.getStatus());
    }

    @Test
    void testPubSub(@NatsConnection Connection connection) {
        //Subscriber 01
        newSubscriber(connection, (message) -> {
            log.info("SUB-01: Local time is '{}'", toMsgString(message));
            message.ack();
        }).subscribe("time.*");

        //Subscriber 02
        newSubscriber(connection, (message) -> {
            log.info("SUB-02: Local time is '{}'", toMsgString(message));
            message.ack();
        }).subscribe("time.*");

        // Publish local time to
        var localTime = localTime();
        var toSubject = "time.local";
        log.info("Publishing Local time '{}' to Subject '{}'", localTime, toSubject);
        connection.publish(newMessage(localTime, toSubject));
    }

    @Test
    void testPubSubAsync(@NatsConnection Connection connection) {
        //Subscriber 01
        newSubscriber(connection, (message) -> {
            log.info("SUB-01: Local time is '{}'", toMsgString(message));
            message.ack();
        }).subscribe("time.*");

        //Subscriber 02
        newSubscriber(connection, (message) -> {
            log.info("SUB-02: Local time is '{}'", toMsgString(message));
            message.ack();
        }).subscribe("time.*");

        // Publish local time to
        var localTime = localTime();
        var toSubject = "time.local";
        log.info("Publishing Local time '{}' to Subject '{}'", localTime, toSubject);
        connection.publish(newMessage(localTime, toSubject));
    }

    @Test
    void testRequestReply(@NatsConnection Connection connection) throws InterruptedException {
        newSubscriber(connection, (message) -> {
            var inbox = message.getReplyTo();
            connection.publish(inbox, localTime().getBytes(UTF_8));
            message.ack();
        }).subscribe("time.local");

        // Publish message
        log.info("Requesting the local time");
        var subject = "time.local";
        var localTime = connection.request(newMessage(subject), Duration.ofSeconds(1));
        log.info("Local time is '{}'", toMsgString(localTime));
    }

    @Test
    void testScatterGather(@NatsConnection Connection connection) {
        newSubscriber(connection, (message) -> {
            var replyToSubject = message.getReplyTo();
            connection.publish(replyToSubject, localTime().getBytes(UTF_8));
            message.ack();
        }).subscribe("time.any");

        newSubscriber(connection, (message) -> {
            var replyToSubject = message.getReplyTo();
            connection.publish(replyToSubject, localDateTime().getBytes(UTF_8));
            message.ack();
        }).subscribe("time.any");

        newSubscriber(connection, (message) -> {
            log.info("SUB-03: Received data '{0}'", toMsgString(message));
            message.ack();
        }).subscribe("time.all");

        // Publish message
        connection.publish("time.any", "time.all", "".getBytes(UTF_8));
    }

    @Test
    void testQueueGroup(@NatsConnection Connection connection) throws InterruptedException {
        //Subscriber 01
        newSubscriber(connection, (message) -> {
            log.info("SUB-01: Local time is '{}'", toMsgString(message));
            message.ack();
        }).subscribe("time.*", QUEUE_GROUP_NAME);

        // Publish local time to
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                connection.publish("time.any", localTime().getBytes(UTF_8));
            }
        }, 0, 1000);

        Thread.sleep(10000L);
    }

    @Disabled
    void testPublishHeaders(@NatsConnection Connection connection) {
        newSubscriber(connection, (message) -> {
            var values = message.getHeaders().get("Publisher");
            log.info("Received message from '{}'", values.get(0));
            log.info("SUB-01: Local time is '{}'", toMsgString(message));
            message.ack();
        }).subscribe("time.*");

        // Publish local time to
        var message = localTime();
        var toSubject = "time.local";

        // https://nats.io/blog/headers-java-client/
        var headers = new Headers().add("Publisher", "P1");

        log.info("Publishing Local time '{}' to Subject '{}'", message, toSubject);
        connection.publish(newMessage(message, toSubject, headers));
    }

}
