package jason.app.symphony.boa.manager;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.session.CustomRedisSessionConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import jason.app.symphony.commons.datasource.TransactionConfig;
import jason.app.symphony.commons.http.session.config.ApplicationRedisConfig;
import jason.app.symphony.security.config.SecurityComponentConfig;


@SpringBootApplication
@Import({ApplicationRedisConfig.class,CustomRedisSessionConfiguration.class,SecurityComponentConfig.class,TransactionConfig.class})
@EnableRedisHttpSession
public class BoaManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoaManagerApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean camelServletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/*");
        registration.setName("CamelServlet");
        return registration;
    }
    
//    @Bean
//    public FilterRegistrationBean tenantFilterRegistrationBean() {
//    		FilterRegistrationBean registration = new FilterRegistrationBean(new TenantDetectionFilter(), camelServletRegistrationBean());
//        registration.setName("tenantFilter");
//        return registration;
//    }

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler(); // single threaded by default
	}
}

