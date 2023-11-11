package nats.learning.core;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static nats.learning.utils.MessageUtils.toMsgString;
import static nats.learning.utils.NatsUtils.newConnection;
import static nats.learning.utils.NatsUtils.newSubscriber;

@Slf4j
public class QueueGroupSubscriber {
    private static final String subject = "time.*";

    public static void main(String[] args) throws InterruptedException, IOException {
        newSubscriber(newConnection(), (message) -> {
            log.info("SUB-02: Local time is '{}'", toMsgString(message));
        }).subscribe(subject, CoreNatsTests.QUEUE_GROUP_NAME);
    }
}