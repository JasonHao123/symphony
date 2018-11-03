package jason.app.symphony.security.comp.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jason.app.symphony.commons.http.model.User;

public class TenantDetectionFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
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
			if("www.xxxx.com:8080".equals(host)) {
				user.setSchema("1");
			}else {
				user.setSchema("");
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
