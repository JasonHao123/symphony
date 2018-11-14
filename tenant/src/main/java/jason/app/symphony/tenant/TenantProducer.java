package jason.app.symphony.tenant;

import java.lang.reflect.Method;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jason.app.symphony.tenant.service.TenantService;

/**
 * The Tenant producer.
 */
public class TenantProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(TenantProducer.class);
    private TenantEndpoint endpoint;

    public TenantProducer(TenantEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody()); 
		String service = ((TenantEndpoint)getEndpoint()).getRemaining();
		TenantService targetService = getEndpoint().getCamelContext().getRegistry().lookupByNameAndType("tenantService",TenantService.class);
		Method method = TenantService.class.getMethod(service, Exchange.class);
		method.invoke(targetService, exchange);  
    }

}
