package jason.app.symphony.security.service.impl;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jason.app.symphony.security.repository.UserRepository;
import jason.app.symphony.security.service.SecurityService;

@Service("securityService")
public class SecurityServiceImpl implements SecurityService {

	@Autowired
	private UserRepository repo;

	@Override
	public void list(Exchange exchange) {
		

	}

}
