package jason.app.symphony.order.comp;

import java.lang.reflect.Method;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jason.app.symphony.order.comp.service.OrderComponentService;

/**
 * The Order producer.
 */
public class OrderProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(OrderProducer.class);
    private OrderEndpoint endpoint;

    public OrderProducer(OrderEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
		String service = ((OrderEndpoint)getEndpoint()).getRemaining();
		OrderComponentService targetService = getEndpoint().getCamelContext().getRegistry().lookupByNameAndType("orderComponentService",OrderComponentService.class);
		Method method = OrderComponentService.class.getMethod(service, Exchange.class);
		method.invoke(targetService, exchange);   
    }

}
