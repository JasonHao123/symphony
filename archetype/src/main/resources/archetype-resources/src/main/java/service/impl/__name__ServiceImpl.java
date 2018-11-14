package ${package}.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ${package}.entity.${name};
import ${package}.repository.${name}Repository;
import ${package}.service.${name}Service;

@Service("${scheme}Service")
public class ${name}ServiceImpl implements ${name}Service {

	@Autowired
	private ${name}Repository repo;
	
	@Override
	public void create(Exchange exchange) {
		${name} model = new ${name}();
		model.setName(exchange.getIn().getBody(String.class));
		repo.save(model);
	}

	@Override
	public void list(Exchange exchange) {
		List<${package}.model.Tenant> result = new ArrayList<${package}.model.Tenant>();
		for(${name} entity:repo.findAll()) {
			${package}.model.${name} model = new ${package}.model.${name}() ;
			model.setId(entity.getId());
			model.setName(entity.getName());
			result.add(model);
		}
		exchange.getOut().setBody(result);
	}

}
