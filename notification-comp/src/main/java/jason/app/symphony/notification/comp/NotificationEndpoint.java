package jason.app.symphony.notification.comp;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

/**
 * Represents a Notification endpoint.
 */
@UriEndpoint(firstVersion = "0.0.1-SNAPSHOT", scheme = "notification", title = "Notification", syntax="notification:name", 
             consumerClass = NotificationConsumer.class, label = "custom")
public class NotificationEndpoint extends DefaultEndpoint {
    @UriPath @Metadata(required = "true")
    private String name;
    @UriParam(defaultValue = "10")
    private int option = 10;

    public NotificationEndpoint() {
    }

    public NotificationEndpoint(String uri, NotificationComponent component) {
        super(uri, component);
    }

    public NotificationEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public Producer createProducer() throws Exception {
        return new NotificationProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        return new NotificationConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }

    /**
     * Some description of this option, and what it does
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Some description of this option, and what it does
     */
    public void setOption(int option) {
        this.option = option;
    }

    public int getOption() {
        return option;
    }
}
