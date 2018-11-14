package jason.app.symphony.tenant.service;

import org.apache.camel.Exchange;

public interface TenantService {
	public void create(Exchange exchange);
	public void list(Exchange exchange);
}
