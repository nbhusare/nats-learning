package nats.learning.jetstream;

import io.nats.client.Connection;
import io.nats.client.JetStreamApiException;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;
import lombok.extern.slf4j.Slf4j;
import nats.learning.ext.annotations.NatsConnection;
import nats.learning.ext.annotations.NatsServerAndConnectionExtension;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nats.learning.utils.MessageUtils.toMsgString;
import static nats.learning.utils.NatsUtils.newSubscriber;
import static nats.learning.utils.TimeUtils.localTime;

/**
 * Test to showcase the Core NATS functionalities - Pub/Sub, Request-Reply, and Queue groups.
 */

@Slf4j
@NatsServerAndConnectionExtension
public class JetStreamTests {

    private static final String STREAM_NAME = "localtime";

    private static final String SUBJECT = "time.local";

    @Test
    void testJetStream(@NatsConnection Connection connection) throws IOException, JetStreamApiException, InterruptedException {
        var jetStream = connection.jetStream();
        addStream(connection);

        // Publish to the JetStream
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                var localTime = localTime();
                log.info("Publishing '{}'", localTime.toString());
                connection.publish(SUBJECT, localTime.getBytes(UTF_8));
            }
        }, 0, 1000);

        // Subscribe to the JetStream
        var subscription = jetStream.subscribe("time.*");
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    var message = subscription.nextMessage(Duration.ofSeconds(1));
                    if (message != null) {
                        log.info("Local time is '{}'", new String(message.getData()));
                        message.ack();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 3000, 1000);

        Thread.sleep(10000L);
    }

    @Test
    void testJetStreamV2(@NatsConnection Connection connection) throws IOException, JetStreamApiException, InterruptedException {
        var jetStream = connection.jetStream();
        addStream(connection);

        // Publish to the JetStream
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                var localTime = localTime();
                log.info("Publishing '{}'", localTime.toString());
                connection.publish(SUBJECT, localTime.getBytes(UTF_8));
            }
        }, 0, 1000);

        Thread.sleep(3000);

        // Subscribe to the JetStream
        jetStream.subscribe("time.*", //
                newSubscriber(connection), //
                (message) -> log.info("Local time is '{}'", toMsgString(message)), //
                true);

        Thread.sleep(10000L);
    }

    private void addStream(Connection connection) {
        try {
            connection.jetStreamManagement().addStream(
                    StreamConfiguration.builder() //
                            .name(STREAM_NAME) //
                            .subjects(SUBJECT) //
                            .storageType(StorageType.Memory) //
                            .build());
        } catch (Exception e) {
            String message = MessageFormat.format("Error loading or creating the stream {0}", STREAM_NAME);
            throw new RuntimeException(message, e);
        }
    }

}
