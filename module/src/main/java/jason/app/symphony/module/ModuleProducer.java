package jason.app.symphony.module;

import java.lang.reflect.Method;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jason.app.symphony.module.service.ModuleService;

/**
 * The Module producer.
 */
public class ModuleProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(ModuleProducer.class);
    private ModuleEndpoint endpoint;

    public ModuleProducer(ModuleEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());
		String service = ((ModuleEndpoint)getEndpoint()).getRemaining();
		ModuleService targetService = getEndpoint().getCamelContext().getRegistry().lookupByNameAndType("moduleService",ModuleService.class);
		Method method = ModuleService.class.getMethod(service, Exchange.class);
		method.invoke(targetService, exchange);  
    }

}
