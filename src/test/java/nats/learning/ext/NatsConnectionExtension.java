package nats.learning.ext;

import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DockerClientBuilder;
import io.nats.client.Connection;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.time.Duration;

import static nats.learning.utils.NatsUtils.newConnection;

public class NatsConnectionExtension extends TypeBasedParameterResolver<Connection> implements AfterAllCallback, BeforeAllCallback {

    private Connection connection;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        connection = newConnection();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        connection.flush(Duration.ZERO);
        connection.close();
    }

    @Override
    public Connection resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return connection;
    }

}
