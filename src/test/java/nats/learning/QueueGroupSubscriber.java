package nats.learning;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static nats.learning.utils.PrintUtils.*;
import static nats.learning.utils.MessageUtils.*;
import static nats.learning.utils.NatsUtils.*;

public class QueueGroupSubscriber {
        private static final String subject = "time.*";

        private static final String queueGroup = "time.local";

        public static void main(String[] args) throws InterruptedException, IOException {
            newSubscriber(newConnection(), (message) -> {
                print("SUB-02, Local time is : {0}", toMsgString(message));
            }).subscribe(subject, queueGroup);
        }
    }