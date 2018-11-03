package jason.app.symphony.security.comp.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jason.app.symphony.commons.http.model.User;
import jason.app.symphony.security.comp.entity.Tenant;
import jason.app.symphony.security.comp.repository.TenantRepository;

public class TenantDetectionFilter extends OncePerRequestFilter{

	@Autowired
	private TenantRepository repo;


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = null;
		if(auth!=null) {
			if(auth.getPrincipal() instanceof AnonymousAuthenticationToken) {
				user = (User) ((AnonymousAuthenticationToken)auth.getPrincipal()).getPrincipal();
			}else if(auth.getPrincipal() instanceof User) {
				user = (User) auth.getPrincipal();
			}
		}
		if(user!=null) {
			
			String host = ((HttpServletRequest)request).getHeader("Host");
			System.out.println(host);
			Tenant tenant = repo.findFirstByDomain(host);
			if(tenant!=null) {
				user.setSchema(tenant.getSchemaName());
			}else {
				user.setSchema("");
			}
		}
		filterChain.doFilter(request, response);
		
	}


}
