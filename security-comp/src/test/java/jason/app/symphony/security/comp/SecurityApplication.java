package jason.app.symphony.security.comp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import jason.app.symphony.commons.http.model.SymphonyUser;
import jason.app.symphony.security.comp.config.SecurityComponentConfig;


@SpringBootApplication
@Import(SecurityComponentConfig.class)
public class SecurityApplication {

	@Bean
	public SymphonyUser user() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
		SymphonyUser user = new SymphonyUser("anonymous","","anonymous",false,false,false,false,authorities);
		user.setSchema("");
	//	AnonymousAuthenticationToken token = new AnonymousAuthenticationToken("23424234", user, authorities);
	//	SecurityContextHolder.getContext().setAuthentication(token);
		return user;
	}
    public static void main(String[] args) {

        SpringApplication.run(SecurityApplication.class, args);
    }

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler(); // single threaded by default
	}

}

