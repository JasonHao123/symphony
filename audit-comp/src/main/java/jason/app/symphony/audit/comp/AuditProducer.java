package jason.app.symphony.audit.comp;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Audit producer.
 */
public class AuditProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(AuditProducer.class);
    private AuditEndpoint endpoint;

    public AuditProducer(AuditEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());    
    }

}
