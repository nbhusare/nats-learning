package nats.learning.core;

import io.nats.client.ConnectionListener;
import io.nats.client.Options;
import lombok.extern.slf4j.Slf4j;
import nats.learning.ext.NatsServerExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static nats.learning.utils.NatsUtils.newConnection;


@ExtendWith(NatsServerExtension.class)
@Slf4j
public class ConnectionListenerTest {
    private static final ConnectionListener LISTENER = (conn, event) -> log.info(event.toString());

    @Test
    void testConnectionListener() throws IOException, InterruptedException {
        var options = Options.builder() //
                .server(Options.DEFAULT_URL) //
                .connectionListener(LISTENER) //
                .build();
        var connection = newConnection(options);
        Assertions.assertNotNull(connection);

        connection.close();
        connection.removeConnectionListener(LISTENER);
    }

}
