package jason.app.symphony.module.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jason.app.symphony.module.entity.Module;
import jason.app.symphony.module.repository.ModuleRepository;
import jason.app.symphony.module.service.ModuleService;

@Service("moduleService")
public class ModuleServiceImpl implements ModuleService {

	@Autowired
	private ModuleRepository repo;
	
	@Override
	public void create(Exchange exchange) {
		Module model = new Module();
		model.setName(exchange.getIn().getBody(String.class));
		repo.save(model);
	}

	@Override
	public void list(Exchange exchange) {
		List<jason.app.symphony.module.model.Module> result = new ArrayList<jason.app.symphony.module.model.Module>();
		for(Module entity:repo.findAll()) {
			jason.app.symphony.module.model.Module model = new jason.app.symphony.module.model.Module() ;
			model.setId(entity.getId());
			model.setName(entity.getName());
			result.add(model);
		}
		exchange.getOut().setBody(result);
	}

}
