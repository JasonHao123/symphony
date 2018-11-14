package jason.app.symphony.module.service;

import org.apache.camel.Exchange;

public interface ModuleService {
	public void create(Exchange exchange);
	public void list(Exchange exchange);
}
