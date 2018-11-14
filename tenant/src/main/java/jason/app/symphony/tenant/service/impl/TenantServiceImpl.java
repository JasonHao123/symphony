package jason.app.symphony.tenant.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jason.app.symphony.tenant.entity.Tenant;
import jason.app.symphony.tenant.repository.TenantRepository;
import jason.app.symphony.tenant.service.TenantService;

@Service("tenantService")
public class TenantServiceImpl implements TenantService {

	@Autowired
	private TenantRepository repo;
	
	@Override
	public void create(Exchange exchange) {
		Tenant tenant = new Tenant();
		tenant.setName(exchange.getIn().getBody(String.class));
		repo.save(tenant);
	}

	@Override
	public void list(Exchange exchange) {
		List<jason.app.symphony.tenant.model.Tenant> result = new ArrayList<jason.app.symphony.tenant.model.Tenant>();
		for(Tenant tenant:repo.findAll()) {
			jason.app.symphony.tenant.model.Tenant model = new jason.app.symphony.tenant.model.Tenant();
			model.setId(tenant.getId());
			model.setName(tenant.getName());
			result.add(model);
		}
		exchange.getOut().setBody(result);
	}

}
