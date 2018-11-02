package jason.app.symphony.order.comp.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import jason.app.symphony.order.comp.service.OrderComponentService;
import jason.app.symphony.order.comp.service.impl.OrderComponentServiceImpl;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "orderEntityManager", transactionManagerRef = "orderTransactionManager", basePackages = "jason.app.symphony.order.comp.repository")
public class OrderComponentConfig {

	@Bean
	public OrderComponentService orderComponentService() {
		OrderComponentServiceImpl impl = new OrderComponentServiceImpl();
		return impl;

	}

	/**
	 * Order datasource definition.
	 * 
	 * @return datasource.
	 */
	@Bean(name="orderDataSource")
	@ConfigurationProperties(prefix = "spring.order.datasource")
	public DataSource orderDataSource() {
		return DataSourceBuilder.create().build();
	}

	/**
	 * Entity manager definition.
	 * 
	 * @param builder
	 *            an EntityManagerFactoryBuilder.
	 * @return LocalContainerEntityManagerFactoryBean.
	 */
	@Bean(name = "orderEntityManager")
	public LocalContainerEntityManagerFactoryBean orderEntityManagerFactory(EntityManagerFactoryBuilder builder,@Qualifier("orderDataSource") DataSource orderDataSource) {
		return builder.dataSource(orderDataSource).properties(hibernateProperties())
				.packages("jason.app.symphony.order.comp.entity").persistenceUnit("securityPU").build();
	}

	/**
	 * @param entityManagerFactory
	 * @return
	 */
	@Bean(name = "orderTransactionManager")
	public PlatformTransactionManager orderTransactionManager(
			@Qualifier("orderEntityManager") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	private Map<String, Object> hibernateProperties() {

		Resource resource = new ClassPathResource("order-hibernate.properties");
		try {
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			return properties.entrySet().stream()
					.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue()));
		} catch (IOException e) {
			return new HashMap<String, Object>();
		}
	}

}
