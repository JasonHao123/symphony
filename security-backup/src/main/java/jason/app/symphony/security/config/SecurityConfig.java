package jason.app.symphony.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import jason.app.symphony.commons.http.model.User;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//	@Bean
//	public SpringSecurityAuthorizationPolicy authorizationPolicy(AuthenticationManager authenticationManager,AccessDecisionManager accessDecisionManager) {
//		SpringSecurityAuthorizationPolicy policy = new SpringSecurityAuthorizationPolicy();
//		policy.setId("user");
//		policy.setAuthenticationManager(authenticationManager);
//		policy.setAccessDecisionManager(accessDecisionManager);
//		policy.setSpringSecurityAccessPolicy(new SpringSecurityAccessPolicy("ROLE_USER"));
//		return policy;
//	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().anonymous().principal("guest").authorities("ROLE_GUEST")
		.and().authorizeRequests().anyRequest().permitAll();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth,UserDetailsService customUserDetailService,PasswordEncoder passwordEncoder) throws Exception {
		auth
		.userDetailsService(customUserDetailService).passwordEncoder(passwordEncoder);
	}

}
