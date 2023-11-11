package nats.learning.ext;

import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DockerClientBuilder;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

public class NatsServerExtension implements BeforeAllCallback, AfterAllCallback, TestExecutionExceptionHandler {

    @Override
    public void beforeAll(ExtensionContext context) {
        startServer(context);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        stopServer(context);
    }

    private void startServer(ExtensionContext context) {
        var client = DockerClientBuilder.getInstance().build();
        var container = client.createContainerCmd("nats:latest") //
                .withPortBindings(PortBinding.parse("4222:4222"), //
                        PortBinding.parse("6222:6222"), //
                        PortBinding.parse("8222:8222"))
                .withCmd("-js")
                .exec();
        client.startContainerCmd(container.getId()).exec();
        getStore(context).put("container-id", container.getId());
    }

    private void stopServer(ExtensionContext context) {
        var client = DockerClientBuilder.getInstance().build();

        var containerId = getStore(context).get("container-id", String.class);
        client.killContainerCmd(containerId).exec();
        client.removeContainerCmd(containerId).exec();
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) {
        stopServer(context);
    }

    private Store getStore(ExtensionContext context) {
        return context.getStore(Namespace.create(getClass()));
    }


}
