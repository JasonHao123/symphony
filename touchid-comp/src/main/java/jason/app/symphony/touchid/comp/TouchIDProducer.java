package jason.app.symphony.touchid.comp;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The TouchID producer.
 */
public class TouchIDProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(TouchIDProducer.class);
    private TouchIDEndpoint endpoint;

    public TouchIDProducer(TouchIDEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());    
    }

}
