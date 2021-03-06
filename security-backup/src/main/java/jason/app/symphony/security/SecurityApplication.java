package jason.app.symphony.security;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.zipkin.starter.CamelZipkin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.session.CustomRedisSessionConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import jason.app.symphony.commons.http.session.config.ApplicationRedisConfig;
import jason.app.symphony.security.comp.config.SecurityComponentConfig;


@SpringBootApplication
@CamelZipkin
@Import({ApplicationRedisConfig.class,SecurityComponentConfig.class,CustomRedisSessionConfiguration.class})
@EnableRedisHttpSession
public class SecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean camelServletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/*");
        registration.setName("CamelServlet");
        return registration;
    }

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler(); // single threaded by default
	}
}

