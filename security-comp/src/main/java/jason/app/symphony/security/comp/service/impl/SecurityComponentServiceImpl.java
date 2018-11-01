package jason.app.symphony.security.comp.service.impl;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jason.app.symphony.security.comp.model.LoginRequest;
import jason.app.symphony.security.comp.model.LoginResponse;
import jason.app.symphony.security.comp.model.LoginResponseBody;
import jason.app.symphony.security.comp.service.SecurityComponentService;


public class SecurityComponentServiceImpl implements SecurityComponentService {

	@Autowired
	private DaoAuthenticationProvider authenticationProvider;
	@Override
	public void login(Exchange exchange) {
		LoginRequest request = (LoginRequest) exchange.getIn().getBody();
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getBody().getUsername(), request.getBody().getPassword());
		LoginResponse response = new LoginResponse();
		LoginResponseBody responseBody = new LoginResponseBody(); 
		response.setBody(responseBody);
		try {
			Authentication auth = authenticationProvider.authenticate(token);
			responseBody.setAuthenticated(auth.isAuthenticated());
			if(auth.isAuthenticated()) {
				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		}catch(Exception e) {
			responseBody.setAuthenticated(false);
		}


		exchange.getOut().setBody(response); 
	}

}
