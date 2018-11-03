package jason.app.symphony.order;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.zipkin.starter.CamelZipkin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.session.CustomRedisSessionConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import jason.app.symphony.commons.http.session.config.ApplicationRedisConfig;
import jason.app.symphony.order.comp.config.OrderComponentConfig;
import jason.app.symphony.security.comp.config.SecurityComponentConfig;
import jason.app.symphony.security.comp.filter.TenantDetectionFilter;


@SpringBootApplication
@CamelZipkin
@Import({ApplicationRedisConfig.class,SecurityComponentConfig.class,CustomRedisSessionConfiguration.class,OrderComponentConfig.class})
@EnableRedisHttpSession
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean camelServletRegistrationBean() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/*");
        registration.setName("CamelServlet");
        return registration;
    }
    
    @Bean
    public FilterRegistrationBean tenantFilterRegistrationBean() {
    		FilterRegistrationBean registration = new FilterRegistrationBean(new TenantDetectionFilter(), camelServletRegistrationBean());
        registration.setName("tenantFilter");
        return registration;
    }

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler(); // single threaded by default
	}
}

