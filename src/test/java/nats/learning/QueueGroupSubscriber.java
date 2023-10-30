package nats.learning;

import java.io.IOException;

import static nats.learning.utils.MessageUtils.toMsgString;
import static nats.learning.utils.NatsUtils.newConnection;
import static nats.learning.utils.NatsUtils.newSubscriber;
import static nats.learning.utils.PrintUtils.print;

public class QueueGroupSubscriber {
    private static final String subject = "time.*";

    private static final String queueGroup = "time.local";

    public static void main(String[] args) throws InterruptedException, IOException {
        newSubscriber(newConnection(), (message) -> {
            print("SUB-02, Local time is : {0}", toMsgString(message));
        }).subscribe(subject, queueGroup);
    }
}