package jason.app.symphony.notification.comp;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Notification producer.
 */
public class NotificationProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationProducer.class);
    private NotificationEndpoint endpoint;

    public NotificationProducer(NotificationEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());    
    }

}
