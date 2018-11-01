package jason.app.symphony.security.comp;

import java.lang.reflect.Method;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jason.app.symphony.security.comp.service.SecurityComponentService;

/**
 * The security producer.
 */
public class SecurityProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(SecurityProducer.class);


    public SecurityProducer(SecurityEndpoint endpoint) {
        super(endpoint);
    }

    public void process(Exchange exchange) throws Exception {
    		String service = ((SecurityEndpoint)getEndpoint()).getRemaining();
    		SecurityComponentService targetService = getEndpoint().getCamelContext().getRegistry().lookupByNameAndType("securityComponentService",SecurityComponentService.class);
    		Method method = SecurityComponentService.class.getMethod(service, Exchange.class);
    		method.invoke(targetService, exchange);  
    }

}
