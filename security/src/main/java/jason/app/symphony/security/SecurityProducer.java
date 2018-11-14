package jason.app.symphony.security;

import java.lang.reflect.Method;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jason.app.symphony.security.service.SecurityService;

/**
 * The Security producer.
 */
public class SecurityProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityProducer.class);
    private SecurityEndpoint endpoint;

    public SecurityProducer(SecurityEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());
		String service = ((SecurityEndpoint)getEndpoint()).getRemaining();
		SecurityService targetService = getEndpoint().getCamelContext().getRegistry().lookupByNameAndType("securityService",SecurityService.class);
		Method method = SecurityService.class.getMethod(service, Exchange.class);
		method.invoke(targetService, exchange);  
    }

}
