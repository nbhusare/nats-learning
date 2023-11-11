package nats.learning.ext.annotations;

import nats.learning.ext.NatsConnectionExtension;
import nats.learning.ext.NatsServerExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({ NatsServerExtension.class, NatsConnectionExtension.class })
public @interface NatsServerAndConnectionExtension {
}
